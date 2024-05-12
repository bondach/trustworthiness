package trustworthiness

import cats.syntax.all._
import cats.effect.std.Random
import cats.effect.{IO, IOApp}
import com.comcast.ip4s._
import io.circe.generic.auto._
import fs2.kafka._
import io.circe.syntax.EncoderOps
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder

import scala.concurrent.duration._

object Stub extends IOApp.Simple {
  val config = new {
    val kafka = new {
      val host = System.getenv("KAFKA_HOST")
      val port = System.getenv("KAFKA_PORT")
      val topic = System.getenv("KAFKA_TOPIC")
    }
    val app = new {
      val host = System.getenv("HOST")
      val port = System.getenv("PORT")
      val minDelay = System.getenv("MIN_DELAY").toInt
      val maxDelay = System.getenv("MAX_DELAY").toInt
    }
  }

  implicit val serRes: Serializer[IO, Result] = Serializer.instance[IO, Result] { (_, _, s) =>
    IO.delay(s.asJson.toString().getBytes("UTF-8"))
  }

  val producerSettings: ProducerSettings[IO, Option[String], Result] =
    ProducerSettings[IO, Option[String], Result]
      .withBootstrapServers(s"${config.kafka.host}:${config.kafka.port}")

  case class Response(requestId: String)
  case class Result(requestId: String, result: String)
  case class Passport(serial: String, number: String)
  case class Phone(country: String, code: String, number: String)
  case class User(name: String, surname: String, patronymic: Option[String], passport: Passport, phoneNumber: Phone)

  implicit val userDecoder: EntityDecoder[IO, User] = jsonOf

  def checkRoute(p: KafkaProducer[IO, Option[String], Result]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "check" => (for {
      _     <- req.attemptAs[User].value
      rnd   <- Random.scalaUtilRandom[IO]
      rId   <- List.fill(7)(0).traverse(_ => rnd.nextAlphaNumeric).map(_.mkString.toUpperCase)
      delay <- rnd.betweenInt(config.app.minDelay, config.app.maxDelay).map(_.seconds)
      res   <- rnd.nextBoolean.map {
        case true => "trust"
        case false => "mistrust"
      }
      _     <- (IO.sleep(delay) >> p.produceOne(config.kafka.topic, None, Result(rId, "trust"))).start
    } yield rId).flatMap(r => Ok(Response(r).asJson))
  }

  override def run: IO[Unit] = (for {
    producer <- KafkaProducer.resource(producerSettings)
    _        <- EmberServerBuilder
                  .default[IO]
                  .withPort(Port.fromString(config.app.port).get)
                  .withHost(Host.fromString(config.app.host).get)
                  .withHttpApp(checkRoute(producer).orNotFound)
                  .build
  } yield ()).use(_ => IO.never)

}