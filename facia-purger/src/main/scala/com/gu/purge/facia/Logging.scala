package com.gu.purge.facia

import org.apache.log4j.Logger

trait Logging {
  lazy val log: Logger = Logger.getLogger(getClass.getName)
}
