resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/maven-releases"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.15")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.0")
