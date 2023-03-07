package com.gu.aws

import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region.EU_WEST_1
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{ PutObjectRequest, PutObjectResponse }
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest

import scala.jdk.CollectionConverters.IterableHasAsScala
class ParameterStore {

  lazy val client: SsmClient = SsmClient.builder()
    .region(AWS.region).credentialsProvider(AWS.credentials)
    .build()

  def unwrapQuotedString(input: String): String = {
    val quotedString = "\"(.*)\"".r
    input match {
      case quotedString(content) => content
      case content => content
    }
  }

  def getPath(path: String, isRecursiveSearch: Boolean = false): Map[String, String] = {

    val parameterRequest = GetParametersByPathRequest.builder()
      .withDecryption(true)
      .path(path)
      .recursive(isRecursiveSearch).build()

    val result = client.getParametersByPathPaginator(parameterRequest)

    val resultMap: Map[String, String] = result.asScala
      .flatMap(_.parameters.asScala)
      .map(param => param.name -> unwrapQuotedString(param.value))
      .toMap

    resultMap
  }
}

class S3 {
  private val s3Client = S3Client.builder()
    .region(EU_WEST_1)
    .build()

  def put(bucket: String, key: String, content: String, kmsKey: String): PutObjectResponse = {
    val bytes = content.getBytes()

    val putObjectRequest = PutObjectRequest.builder
      .bucket(bucket).key(key)
      .contentLength(bytes.length).ssekmsKeyId(kmsKey).build()

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes))
  }
}
