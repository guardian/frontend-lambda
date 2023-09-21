package com.gu.backupparameterstore

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.ConfigEvent
import com.gu.aws.{ ParameterStore, S3 }
import com.typesafe.scalalogging.LazyLogging

case class Env(app: String, stack: String, stage: String) {
  override def toString: String = s"App: $app, Stack: $stack, Stage: $stage\n"
}

object Env {
  def apply(): Env = {
    def env(name: String): String = Option(System.getenv(name)).getOrElse("DEV")
    Env(env("App"), env("Stack"), env("Stage"))
  }
}

object Lambda extends LazyLogging {
  val env = Env()
  val backupService = new BackupService(new ParameterStore, new S3, env)

  def handler(lambdaInput: ConfigEvent, context: Context): Unit = {
    logger.info(s"Starting $env")
    backupService.backupParameterStore()
  }

  def main(args: Array[String]): Unit = {
    Lambda.backupService.backupParameterStore()
  }

}
