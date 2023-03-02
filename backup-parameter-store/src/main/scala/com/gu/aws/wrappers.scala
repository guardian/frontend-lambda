package com.gu.aws

import java.io.ByteArrayInputStream
import com.amazonaws.auth.{ AWSCredentialsProviderChain, InstanceProfileCredentialsProvider }
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.{ ObjectMetadata, PutObjectRequest, PutObjectResult, SSEAwsKeyManagementParams }
import com.amazonaws.services.simplesystemsmanagement.{ AWSSimpleSystemsManagement, AWSSimpleSystemsManagementClientBuilder }
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest

import scala.jdk.CollectionConverters._
import scala.annotation.tailrec

object AwsConfig {
  val provider = new AWSCredentialsProviderChain(
    new ProfileCredentialsProvider("frontend"),
    InstanceProfileCredentialsProvider.getInstance())
  val region = "eu-west-1"
  val kmsKeyAlias: String = "arn:aws:kms:eu-west-1:642631414762:alias/FrontendConfigKey"
}

class ParameterStore {

  private lazy val client: AWSSimpleSystemsManagement = AWSSimpleSystemsManagementClientBuilder
    .standard()
    .withRegion(AwsConfig.region)
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

      val parameterRequest = new GetParametersByPathRequest()
        .withWithDecryption(true)
        .withPath(path)
        .withRecursive(isRecursiveSearch)

      val parameterRequestWithNextToken = nextToken.map(parameterRequest.withNextToken).getOrElse(parameterRequest)

      val result = client.getParametersByPath(parameterRequestWithNextToken)

      val resultMap = result.getParameters.asScala.map { param =>
        param.getName -> unwrapQuotedString(param.getValue)
      }.toMap

      Option(result.getNextToken) match {
        case Some(next) => paginate(accum ++ resultMap, Some(next))
        case None => accum ++ resultMap
      }
    }

    paginate(Map.empty, None)
  }
}

class S3 {
  private val s3Client = AmazonS3ClientBuilder
    .standard()
    .withRegion(AwsConfig.region)
    .build()

  def put(bucket: String, key: String, content: String, kmsKey: String): PutObjectResult = {
    val bytes = content.getBytes()
    val metadata = new ObjectMetadata()
    metadata.setContentLength(bytes.length)

    val putObjectRequest = new PutObjectRequest(bucket, key, new ByteArrayInputStream(bytes), metadata)
      .withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams(kmsKey))

    s3Client.putObject(putObjectRequest)
  }
}
