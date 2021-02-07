ThisBuild / organization := "g42"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.3"

val akkaVersion = "2.6.6"
val cats = Seq("org.typelevel" %% "cats-core" % "2.1.1", "org.typelevel" %% "cats-effect" % "2.1.3")
val akkaStream = Seq("com.typesafe.akka" %% "akka-stream" % akkaVersion)
val akka = akkaStream ++ Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "ch.megard" %% "akka-http-cors" % "1.1.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.1",
)

val flyway = Seq("org.flywaydb" % "flyway-core" % "6.4.0")
val scalaTest = Seq("org.scalatest" %% "scalatest" % "3.1.1")
val doobieVersion = "0.8.6"
val doobie = Seq(
  "org.tpolecat" %% "doobie-core",
  "org.tpolecat" %% "doobie-h2",
  "org.tpolecat" %% "doobie-postgres",
  "org.tpolecat" %% "doobie-hikari",
).map(_ % doobieVersion)

val doobieTest = Seq("org.tpolecat" %% "doobie-scalatest" % doobieVersion).map(_ % Test)
val log = Seq("ch.qos.logback" % "logback-classic" % "1.2.3", "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2")
val scalaCheck = Seq(
  "org.scalacheck" %% "scalacheck" % "1.14.3",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.5",
  "com.danielasfregola" %% "random-data-generator" % "2.8",
).map(_ % Test)

/**
  * Tapir for API documenation
  */
val tapirVersion = "0.16.16"
val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion
val tapirCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion
val tapirOpenApiCirceYaml = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion
val tapirOpenApiDocs = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion
val tapirSwaggerUiAkka = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion
val tapirAkkaServer = "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion
val tapir =
  Seq(tapirCore, tapirCirce, tapirSwaggerUiAkka, tapirAkkaServer, tapirOpenApiDocs, tapirOpenApiCirceYaml, tapirCirce)

/**
  * Global settings
  */
lazy val commonSettings: Seq[Setting[_]] =
  Seq(scalacOptions ++= Seq("-deprecation"), resolvers ++= Seq(Resolver.jcenterRepo))

val coreDependencies = cats
val infraDependencies = akka ++ doobie ++ flyway ++ scalaCheck ++ scalaTest ++ doobieTest ++ tapir ++ log

/**
  * Project definition
  */
lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= coreDependencies,
    commonSettings,
  )

lazy val infra = (project in file("infra"))
  .dependsOn(core)
  .settings(
    name := "infra",
    libraryDependencies ++= infraDependencies,
    commonSettings,
  )

val root = (project in file("."))
  .dependsOn(infra)
  .aggregate(core)
  .settings(name := "g42-test")
