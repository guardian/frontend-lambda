package com.gu.purge.facia

import org.parboiled2.{ Parser, ParserInput }

import scala.util.Success

/*
 * Parse the name of a front from the full S3 path
 * e.g.
 *   - DEV/frontsapi/pressed/live/au/sport/fapi/pressed.json -> au/sport
 *   - PROD/frontsapi/pressed/live/uk/fapi/pressed.json -> uk
 */
class FrontsS3PathParser(stage: String, val input: ParserInput) extends Parser with Logging {
  def prefix = rule { s"$stage/frontsapi/pressed/live/" }
  val suffix = "/fapi/pressed.json"

  // Capture the front by using a negative lookahead for the suffix
  def front = rule { capture(oneOrMore(!suffix ~ ANY)) }
  def expr = rule { prefix ~ front ~ suffix ~ EOI }

  def run(): Option[String] = expr.run() match {
    case Success(matched) => Some(matched)
    case x => {
      log.error(s"error: $x")
      None
    }
  }
}

