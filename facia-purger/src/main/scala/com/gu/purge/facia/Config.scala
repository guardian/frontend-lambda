package com.gu.purge.facia
import java.util.Properties
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.profile._
import scala.util.Try

case class Config(fastlyServiceId: String, fastlyApiKey: String)

object Config extends Logging {

  val s3 = new AmazonS3Client(new ProfileCredentialsProvider("frontend"))

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