import sbt._

object Dependencies {
  lazy val sparkVersion = "3.2.1"
  lazy val testVersion = "3.2.11"

  lazy val dependencies = Seq(
    configDeps,
    loggingDeps,
    sparkDeps,
    testingDeps,
    gcpDeps
  ).flatten

  lazy val configDeps = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.14.1",
    "com.typesafe" % "config" % "1.4.2"
  )

  lazy val loggingDeps = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  )

  lazy val sparkDeps = Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
  )

  lazy val testingDeps = Seq(
    "org.scalactic" %% "scalactic" % testVersion,
    "org.scalatest" %% "scalatest" % testVersion % "test",
    "org.scalacheck" %% "scalacheck" % "1.14.3"
  )

  lazy val gcpDeps = Seq(
    "com.google.cloud" % "google-cloud-dataproc" % "2.3.2" % "provided",
    "com.google.cloud" % "google-cloud-storage" % "2.4.2"
  )

}
