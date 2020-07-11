ThisBuild / organization := "com.badr"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.3"

val akkaVersion = "2.6.6"
val cats = Seq("org.typelevel" %% "cats-core" % "2.1.1", "org.typelevel" %% "cats-effect" % "2.1.3")
val akkaStream = Seq("com.typesafe.akka" %% "akka-stream" % akkaVersion)
val akka = akkaStream ++ Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.1",
)

val akkaHttp = akkaStream ++ Seq("com.typesafe.akka" %% "akka-http" % "10.1.12")
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

val scalaCheck = Seq(
  "org.scalacheck" %% "scalacheck" % "1.14.3",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.5",
  "com.danielasfregola" %% "random-data-generator" % "2.8",
).map(_ % Test)

/**
  * Global settings
  */
lazy val commonSettings: Seq[Setting[_]] =
  Seq(scalacOptions ++= Seq("-deprecation"), resolvers ++= Seq(Resolver.jcenterRepo))

val toolsDependencies = Seq()
val coreDependencies = cats
val infraDependencies = akka ++ doobie ++ flyway ++ scalaCheck ++ scalaTest ++ doobieTest
val webDependencies = akkaHttp

/**
  * Project definition
  */
lazy val tools = (project in file("tools"))
  .settings(
    name := "tools",
    libraryDependencies ++= toolsDependencies,
    commonSettings,
  )

lazy val core = (project in file("core"))
  .dependsOn(tools)
  .settings(
    name := "core",
    libraryDependencies ++= coreDependencies,
    commonSettings,
  )

lazy val infra = (project in file("infra"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    name := "infra",
    libraryDependencies ++= infraDependencies,
    commonSettings,
  )

lazy val web = (project in file("web"))
  .dependsOn(core % "compile->compile;test->test", infra)
  .settings(name := "web", libraryDependencies ++= webDependencies, commonSettings)

val root = (project in file("."))
  .dependsOn(web)
  .aggregate(tools, core, infra, web) // send commands to every module
  .settings(name := "imdb-streamer")
