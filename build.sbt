import Dependencies._

ThisBuild / scalaVersion := "2.12.12"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "io.opentargets"
ThisBuild / organizationName := "opentargets"

lazy val root = (project in file("."))
  .settings(
    name := "spark_converter",
    Compile / run / mainClass := Some("io.opentargets.spark_converter.Main"),
    Compile / packageBin / mainClass := Some("io.opentargets.spark_converter.Main"),
    libraryDependencies ++= dependencies,
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", "services", "org.apache.hadoop.fs.FileSystem") =>
        MergeStrategy.filterDistinctLines
      case PathList("META-INF", "services", "org.apache.spark.sql.sources.DataSourceRegister") =>
        MergeStrategy.concat
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case _                             => MergeStrategy.first
    }
  )
