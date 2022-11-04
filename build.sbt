ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.9"
ThisBuild / libraryDependencies ++= {
  val akkaHttpV = "10.4.0"
  val akkaV = "2.7.0"
  val scalaTestV = "3.2.14"
  val circeV = "0.14.3"
  val akkaHttpCirceV = "1.39.2"

  Seq(
    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-parser" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test"
  ) ++ Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test",
    "org.apache.kafka" %% "kafka-clients" % "2.1.0",
    "io.confluent" %% "kafka-avro-serializer" % "5.3.0",
    "com.couchbase.client" % "scala-client" % "1.2.4",
    "com.lihaoyi" %% "requests" % "0.7.1",
    "org.mockito" %% "mockito-scala" % "1.17.12" % Test
  ).map(_.cross(CrossVersion.for3Use2_13))
}

lazy val root = (project in file("."))
  .settings(
    name := "user-account-with-bets-service"
  )

