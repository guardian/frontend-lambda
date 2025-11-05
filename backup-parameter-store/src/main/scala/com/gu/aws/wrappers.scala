package com.gu.aws

import software.amazon.awssdk.core.sync.RequestBody

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{ PutObjectRequest, PutObjectResponse, ServerSideEncryption }
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest

import scala.jdk.CollectionConverters._
import scala.annotation.tailrec

object AwsConfig {
  val region = "eu-west-1"
  val kmsKeyAlias: String = "arn:aws:kms:eu-west-1:642631414762:alias/FrontendConfigKey"
}

class ParameterStore {

  private lazy val client: SsmClient = SsmClient
    .builder()
    .region(Region.of(AwsConfig.region))
    .build()

  def unwrapQuotedString(input: String): String = {
    val quotedString = "\"(.*)\"".r
    input match {
      case quotedString(content) => content
      case content => content
    }
  }

  def getPath(path: String, isRecursiveSearch: Boolean = false): Map[String, String] = {

    @tailrec
    def paginate(accum: Map[String, String], nextToken: Option[String]): Map[String, String] = {

      val parameterRequest = GetParametersByPathRequest
        .builder()
        .withDecryption(true)
        .path(path)
        .recursive(isRecursiveSearch)
        .build()

      val parameterRequestWithNextToken = nextToken
        .map(token => parameterRequest
          .toBuilder.nextToken(token)
          .build())
        .getOrElse(parameterRequest)

      val result = client.getParametersByPath(parameterRequestWithNextToken)

      val resultMap = result.parameters().asScala.map { param =>
        param.name() -> unwrapQuotedString(param.value)
      }.toMap

      Option(result.nextToken()) match {
        case Some(next) => paginate(accum ++ resultMap, Some(next))
        case None => accum ++ resultMap
      }
    }

    paginate(Map.empty, None)
  }
}

class S3 {
  private val s3Client = S3Client
    .builder()
    .region(Region.of(AwsConfig.region))
    .build()

  def put(bucket: String, key: String, content: String, kmsKey: String): PutObjectResponse = {
    val bytes = content.getBytes()

    val putObjectRequest = PutObjectRequest.builder()
      .bucket(bucket)
      .key(key)
      .contentLength(bytes.length)
      .serverSideEncryption(ServerSideEncryption.AWS_KMS)
      .ssekmsKeyId(kmsKey)
      .build()

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes))
  }
}
