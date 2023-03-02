package com.gu.aws

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{ AWSCredentialsProviderChain, InstanceProfileCredentialsProvider }
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest
import com.amazonaws.services.simplesystemsmanagement.{ AWSSimpleSystemsManagement, AWSSimpleSystemsManagementClientBuilder }
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region.EU_WEST_1
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{ PutObjectRequest, PutObjectResponse }

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

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
