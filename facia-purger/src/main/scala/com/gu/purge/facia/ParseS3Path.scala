package com.gu.purge.facia

import org.parboiled2.{Parser, ParserInput, Rule1}

import scala.util.Success

// /aws-frontend-store/CODE/frontsapi/pressed/live/au/fapi/pressed.json
class ParseS3Path(stage: String, val input: ParserInput) extends Parser {
  def prefix = rule { s"$stage/frontsapi/pressed/live/" }
  val suffix = "/fapi/pressed.json"

  // Capture the front by using a negative lookahead for the suffix
  def front = rule { capture(oneOrMore(!suffix ~ ANY)) }
  def expr = rule { prefix ~ front ~ suffix ~ EOI }

  def apply() = expr.run() match {
    case Success(matched) => Some(matched)
    case x => { println(s"error: $x"); None }
  }
}
