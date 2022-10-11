ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.9"
ThisBuild / libraryDependencies ++= {
  val akkaHttpV = "10.2.10"
  val akkaV = "2.6.20"
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
    "com.couchbase.client" % "scala-client" % "1.2.4"
  ).map(_.cross(CrossVersion.for3Use2_13))
}

lazy val root = (project in file("."))
  .settings(
    name := "user-account-with-bets-service"
  )

