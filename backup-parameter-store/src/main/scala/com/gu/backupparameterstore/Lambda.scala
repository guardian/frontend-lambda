package com.gu.backupparameterstore

import com.amazonaws.services.lambda.runtime.Context
import com.gu.aws.{ ParameterStore, S3 }
import com.typesafe.scalalogging.LazyLogging

/**
 * This is compatible with aws' lambda JSON to POJO conversion.
 * You can test your lambda by sending it the following payload:
 * {"name": "Bob"}
 */
class LambdaInput() {
  var name: String = _
  def getName(): String = name
  def setName(theName: String): Unit = name = theName
}

case class Env(app: String, stack: String, stage: String) {
  override def toString: String = s"App: $app, Stack: $stack, Stage: $stage\n"
}

object Env {
  def apply(): Env = Env(
    Option(System.getenv("App")).getOrElse("DEV"),
    Option(System.getenv("Stack")).getOrElse("DEV"),
    Option(System.getenv("Stage")).getOrElse("DEV"))
}

object Lambda extends LazyLogging {

  val env = Env()
  val backupService = new BackupService(new ParameterStore, new S3, env)

  def handler(lambdaInput: LambdaInput, context: Context): Unit = {
    logger.info(s"Starting $env")
    backupService.backupParameterStore()
  }

}

object TestIt {
  def main(args: Array[String]): Unit = {
    Lambda.backupService.backupParameterStore()
  }
}
