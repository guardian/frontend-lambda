package com.gu.purge.facia

import com.amazonaws.services.lambda.runtime.{ Context, RequestHandler }
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity
import okhttp3._
import org.apache.commons.codec.digest.DigestUtils

import scala.collection.JavaConverters._

class Lambda(stage: String = "DEV" /*TODO*/) extends RequestHandler[S3Event, String] {

  private lazy val config = Config.load(stage)
  private lazy val httpClient = new OkHttpClient()

  override def handleRequest(event: S3Event, context: Context) = {
    println(s"starting up")

    val entities: List[S3Entity] = event.getRecords.asScala.map(_.getS3).toList

    println(s"Processing ${entities.size} updated entities ...")

    entities.foreach { entity =>
      new ParseS3Path(stage, entity.getObject.getKey).apply() map { id =>
        sendPurgeRequest(id)
      }
    }

    println(s"Finished.")
    ""
  }

  // OkHttp requires a media type even for an empty POST body
  private val EmptyJsonBody: RequestBody =
    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "")

  /**
   * Send a soft purge request to Fastly API.
   *
   * @return whether a piece of content was purged or not
   */
  private def sendPurgeRequest(contentId: String): Boolean = {
    val contentPath = s"/$contentId"
    val surrogateKey = DigestUtils.md5Hex(contentPath)
    val url = s"https://api.fastly.com/service/${config.fastlyServiceId}/purge/$surrogateKey"

    val request = new Request.Builder()
      .url(url)
      .header("Fastly-Key", config.fastlyApiKey)
      .header("Fastly-Soft-Purge", "1")
      .post(EmptyJsonBody)
      .build()

    if (stage == "PROD") {
      val response = httpClient.newCall(request).execute()
      println(s"Sent purge request for content with ID [$contentId]. Response from Fastly API: [${response.code}] [${response.body.string}]")
      response.code == 200
    } else {
      println(s"Didn't sent purge request for content with ID [$contentId].")
      false
    }
  }

}