package com.gu.backupparameterstore

import java.text.SimpleDateFormat
import java.util.Date
import com.gu.aws._
import com.typesafe.config.{ ConfigFactory, ConfigRenderOptions }
import com.typesafe.scalalogging.LazyLogging
import scala.jdk.CollectionConverters._

class BackupService(parameterStore: ParameterStore, s3: S3, env: Env) extends LazyLogging {

  def backupParameterStore(): Unit = {
    val config: String = findConfig()
    logger.info(s"Config found, length ${config.length}")
    putConfig(config)
    logger.info(s"Config backed up successfully")
  }

  private def putConfig(config: String) = {
    val date = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date())
    val keyName = s"config/frontend-config-${env.stage}-$date.conf"
    logger.info(s"Backing up config, key $keyName")
    s3.put("aws-frontend-backup", keyName, config, AwsConfig.kmsKeyAlias)
  }

  private def findConfig() = {
    logger.info("Finding config from parameter store...")
    val params = parameterStore.getPath("/frontend", isRecursiveSearch = true)
    val config = ConfigFactory.parseMap(params.asJava)
    val renderOptions = ConfigRenderOptions
      .defaults()
      .setComments(false)
      .setOriginComments(false)
    config.root.render(renderOptions)
  }
}
