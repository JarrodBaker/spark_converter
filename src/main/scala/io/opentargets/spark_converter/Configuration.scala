package io.opentargets.spark_converter

sealed trait Conversion

trait IO {
  val input: String
  val output: String
  val partitionBy: Option[Seq[String]]
}

case class Json2Parquet(input: String, output: String, partitionBy: Option[Seq[String]])
    extends Conversion
    with IO
case class Parquet2Json(input: String, output: String, partitionBy: Option[Seq[String]])
    extends Conversion
    with IO

case class SparkCnf(uri: Option[String], writeMode: String)

case class Configuration(
    inputs: List[Conversion],
    spark: SparkCnf
)
