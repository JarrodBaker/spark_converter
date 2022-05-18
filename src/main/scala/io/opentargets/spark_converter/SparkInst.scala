package io.opentargets.spark_converter

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import org.apache.spark.SparkConf
import com.typesafe.scalalogging.LazyLogging

class SparkInst(conf: SparkCnf) extends LazyLogging {
  implicit lazy val sc: SparkConf = {
    val sparkConf: SparkConf = new SparkConf()

    // if some uri then setmaster must be set otherwise
    // it tries to get from env if any yarn running
    conf.uri match {
      case Some(uri) if uri.nonEmpty => sparkConf.setMaster(uri)
      case _                         => sparkConf
    }

  }

  implicit lazy val ss: SparkSession = SparkSession.builder
    .appName("spark-converter")
    .config(sc)
    .getOrCreate()

  def convertJsonToParquet(conf: Json2Parquet): Unit =
    convert(conf, "json", "parquet")

  def convertParquetToJson(conf: Parquet2Json): Unit =
    convert(conf, "parquet", "json")

  private def convert(conf: IO, oldFormat: String, newFormat: String): Unit = {
    logger.info(s"Converting ${conf.input} to $newFormat")
    val inputDf = ss.read.format(oldFormat).load(conf.input)
    write(inputDf, conf.output, newFormat, conf.partitionBy)
    logger.info(s"${conf.input} converted to $newFormat. Files at ${conf.output}")

  }

  private def write(
      df: DataFrame,
      path: String,
      format: String,
      partition: Option[Seq[String]]
  ): Unit = {
    partition match {
      case None => df.write.format(format).mode(conf.writeMode).save(path)
      case Some(part) =>
        df.write.format(format).partitionBy(part: _*).mode(conf.writeMode).save(path)
    }
  }
}
