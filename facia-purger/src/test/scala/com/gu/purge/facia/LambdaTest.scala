package com.gu.purge.facia

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.event.S3EventNotification._

import scala.collection.JavaConverters._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import org.scalatest.Matchers._

class LambdaTest extends FlatSpec with MockitoSugar {

  val key = "DEV/frontsapi/pressed/live/au/fapi/pressed.json"
  val req = new RequestParametersEntity("a")
  val resp = new ResponseElementsEntity("s", "d")
  val user = new UserIdentityEntity("f")
  val bucket = new S3BucketEntity("g", user, "h")
  val obj = new S3ObjectEntity(key, 0L, "j", "k")
  val s3 = new S3Entity("l", bucket, obj, "q")
  val record: S3EventNotificationRecord = new S3EventNotificationRecord("w", "e", "r", "2010-06-30T01:20+02:00", "y", req, resp, s3, user)

  val mockContext = mock[Context]
  val lambda = new Lambda()
  lambda.stage = "DEV"

  "The lambda" should "complete and return true given a valid key" in {
    lambda.handleRequest(new S3Event(List[S3EventNotificationRecord](record).asJava), mockContext) should be(true)
  }

}
