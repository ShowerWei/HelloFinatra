name := "fitman"

version := "1.0"

scalaVersion := "2.11.7"

lazy val versions = new {
  val finatra = "2.1.6"
  val logback = "1.1.3"
  val guice = "4.0"
  val mongo_scala_driver = "1.1.1"
  val typesafe_config = "1.3.0"
  val bijection = "0.9.2"
  val swagger = "0.5.1"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"
)


libraryDependencies ++= Seq(
  "com.twitter.finatra" %% "finatra-http" % versions.finatra,
  "com.twitter.finatra" %% "finatra-slf4j" % versions.finatra,
  "com.twitter.inject" %% "inject-core" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test" classifier "tests",

  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  "org.mongodb.scala" %% "mongo-scala-driver" % versions.mongo_scala_driver,
  "com.typesafe" % "config" % versions.typesafe_config,

  // https://mvnrepository.com/artifact/com.twitter/bijection-core_2.11
  "com.twitter" % "bijection-core_2.11" % versions.bijection,

  // https://mvnrepository.com/artifact/com.twitter/bijection-util_2.11
  "com.twitter" % "bijection-util_2.11" % versions.bijection,

  "com.github.xiaodongw" %% "swagger-finatra2" % versions.swagger
)

cancelable in Global := true