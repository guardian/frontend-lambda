package com.gu.purge.facia

import java.util.Properties

import scala.util.Try
import com.amazonaws.auth.profile._
import com.amazonaws.auth.{ AWSCredentialsProviderChain, EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider, SystemPropertiesCredentialsProvider }
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client

case class Config(fastlyServiceId: String, fastlyApiKey: String)

object Config extends Logging {

  val credentialsProviderChain = new AWSCredentialsProviderChain(
    new EnvironmentVariableCredentialsProvider,
    new SystemPropertiesCredentialsProvider,
    new ProfileCredentialsProvider("frontend"),
    new InstanceProfileCredentialsProvider
  )

  val s3: AmazonS3Client = new AmazonS3Client(credentialsProviderChain).withRegion(Regions.EU_WEST_1)

  def load(stage: String): Config = {
    log.info("Loading facia-purger config...")
    val properties = loadProperties("aws-frontend-store", s"$stage/config/facia-purger.properties") getOrElse sys.error("Could not load config file from s3. This lambda will not run.")

    val fastlyServiceId = getMandatoryConfig(properties, "fastly.serviceId")
    log.debug(s"Fastly service ID = $fastlyServiceId")

    val fastlyApiKey = getMandatoryConfig(properties, "fastly.apiKey")
    log.debug(s"Fastly API key = ${fastlyApiKey.take(3)}...")

    Config(fastlyServiceId, fastlyApiKey)
  }

  private def loadProperties(bucket: String, key: String): Try[Properties] = {
    val inputStream = s3.getObject(bucket, key).getObjectContent()
    val properties: Properties = new Properties()
    val result = Try(properties.load(inputStream)).map(_ => properties)
    inputStream.close()
    result
  }

  private def getMandatoryConfig(config: Properties, key: String) =
    Option(config.getProperty(key)) getOrElse sys.error(s"''$key' property missing.")

}