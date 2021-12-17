package com.gu.purge.facia

import org.apache.logging.log4j.{ LogManager, Logger }

trait Logging {
  lazy val log: Logger = LogManager.getLogger(getClass.getName)
}
