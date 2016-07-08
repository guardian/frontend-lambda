package com.gu.purge.facia

import com.amazonaws.services.lambda.runtime.{ CognitoIdentity, ClientContext, LambdaLogger, Context }
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.event.S3EventNotification
import com.amazonaws.services.s3.event.S3EventNotification._
import scala.collection.JavaConverters._

object LambdaTest {

  val data = """{
               |  "Records": [
               |    {
               |      "eventVersion": "2.0",
               |      "eventTime": "1970-01-01T00:00:00.000Z",
               |      "requestParameters": {
               |        "sourceIPAddress": "127.0.0.1"
               |      },
               |      "s3": {
               |        "configurationId": "testConfigRule",
               |        "object": {
               |          "eTag": "0123456789abcdef0123456789abcdef",
               |          "sequencer": "0A1B2C3D4E5F678901",
               |          "key": "DEV/frontsapi/pressed/live/au/fapi/pressed.json",
               |          "size": 1024
               |        },
               |        "bucket": {
               |          "arn": "arn:aws:s3:::mybucket",
               |          "name": "sourcebucket",
               |          "ownerIdentity": {
               |            "principalId": "EXAMPLE"
               |          }
               |        },
               |        "s3SchemaVersion": "1.0"
               |      },
               |      "responseElements": {
               |        "x-amz-id-2": "EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH",
               |        "x-amz-request-id": "EXAMPLE123456789"
               |      },
               |      "awsRegion": "us-east-1",
               |      "eventName": "ObjectCreated:Put",
               |      "userIdentity": {
               |        "principalId": "EXAMPLE"
               |      },
               |      "eventSource": "aws:s3"
               |    }
               |  ]
               |}"""

  val key = "DEV/frontsapi/pressed/live/au/fapi/pressed.json"

  val req = new RequestParametersEntity("a")
  val resp = new ResponseElementsEntity("s", "d")
  val user = new UserIdentityEntity("f")
  val bucket = new S3BucketEntity("g", user, "h")
  val obj = new S3ObjectEntity(key, 0L, "j", "k")
  val s3 = new S3Entity("l", bucket, obj, "q")
  val record: S3EventNotificationRecord = new S3EventNotificationRecord("w", "e", "r", "2010-06-30T01:20+02:00", "y", req, resp, s3, user)

  def main(args: List[String]) = {
    new Lambda().handleRequest(new S3Event(List[S3EventNotificationRecord](record).asJava), new Context() {
      override def getIdentity: CognitoIdentity = ???

      override def getLogStreamName: String = ???

      override def getClientContext: ClientContext = ???

      override def getLogger: LambdaLogger = ???

      override def getMemoryLimitInMB: Int = ???

      override def getInvokedFunctionArn: String = ???

      override def getRemainingTimeInMillis: Int = ???

      override def getAwsRequestId: String = ???

      override def getFunctionVersion: String = ???

      override def getFunctionName: String = ???

      override def getLogGroupName: String = ???
    })
  }

}
