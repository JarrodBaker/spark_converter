package io.opentargets.spark_converter

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigReader

class ConfigurationTest extends AnyFlatSpecLike with Matchers {

  "Pureconfig" should "successfully load standard configuration without error" in {
    val conf: ConfigReader.Result[Configuration] = Main.config
    val msg = conf match {
      case Right(_) =>
        None
      case Left(ex) =>
        Some(ex.toList.foldLeft("")((acc, nxt) => s"{acc} \n ${nxt.description}"))
    }

    assert(conf.isRight, s"Failed with ${msg.getOrElse("")}")
  }

}
