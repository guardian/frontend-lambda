package com.gu.purge.facia

case class Config(fastlyServiceId: String, fastlyApiKey: String)

object Config extends Logging {

  def load(stage: String): Config = {
    log.info("Loading facia-purger config...")

    val config = for {
      fastlyApiKey <- Option(System.getenv("FastlyAPIKey"))
      fastlyServiceId <- Option(System.getenv("FastlyServiceId"))
    } yield {
      Config(fastlyServiceId, fastlyApiKey)
    }

    config.getOrElse(sys.error("Missing environment variable - FastlyAPIKey & FastlyServiceId must be provided"))
  }
}