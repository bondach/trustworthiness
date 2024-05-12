FROM sbtscala/scala-sbt:eclipse-temurin-focal-17.0.10_7_1.9.9_2.13.12
WORKDIR app
COPY . .
RUN sbt compile
ENTRYPOINT ["sbt", "run"]