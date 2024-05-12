ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "trustworthiness"

lazy val root = (project in file("."))
  .settings(
    name := "trustworthiness",
    libraryDependencies ++= Seq(
      "com.github.fd4s" %% "fs2-kafka" % "3.5.0",
      "org.http4s" %% "http4s-ember-server" % "0.23.26",
      "org.http4s" %% "http4s-dsl" % "0.23.26",
      "org.http4s" %% "http4s-circe" % "0.23.26",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-literal" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      "ch.qos.logback" % "logback-classic" % "1.5.3",
      "org.scalatest" %% "scalatest" % "3.2.3" % Test,
    )
  )