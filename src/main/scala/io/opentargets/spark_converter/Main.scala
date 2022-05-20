package io.opentargets.spark_converter

import pureconfig.ConfigSource
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.config.ConfigFactory
import org.apache.spark

object Main extends LazyLogging {

  import pureconfig._
  import pureconfig.generic.auto._
  
  lazy val config: ConfigReader.Result[Configuration] = {
      logger.info("Loading conversion configuration from file.")
      val config = ConfigFactory.load()

      val obj = ConfigSource.fromConfig(config).load[Configuration]
      logger.info(s"Loaded configuration: ${obj.toString}")

      obj
  }

  def main(args: Array[String]): Unit = {
    logger.info("Spark converter starting.")
    
    config.map { conf =>
      {
        lazy val sparkRunner: SparkInst = new SparkInst(conf.spark)
        conf.inputs.map(conv =>
          conv match {
            case a: Parquet2Json => sparkRunner.convertParquetToJson(a)
            case b: Json2Parquet => sparkRunner.convertJsonToParquet(b)
          }
        )
      }
    }
  }
}
