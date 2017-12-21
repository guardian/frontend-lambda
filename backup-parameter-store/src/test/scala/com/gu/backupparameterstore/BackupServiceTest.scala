package com.gu.backupparameterstore

import com.amazonaws.services.s3.model.PutObjectResult
import org.mockito.Mockito._
import com.gu.aws.{ ParameterStore, S3 }
import org.scalatest.{ FlatSpec, Matchers }
import org.scalatest.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{ eq => eqq, _ }

class BackupServiceTest extends FlatSpec with MockitoSugar with Matchers {
  "backupParameterStore" should "find parameters from parameter store and put them in S3" in {
    val parameterStore = mock[ParameterStore]
    val s3 = mock[S3]
    val result = mock[PutObjectResult]
    val expectedPut =
      """
        |{
        |    "foo" : "bar",
        |    "hello" : "world"
        |}
      """.stripMargin

    def putContentMatcher: String = argThat[String](_.replaceAll("\\s", "") == expectedPut.replaceAll("\\s", ""))
    when(parameterStore.getPath("/frontend", isRecursiveSearch = true)) thenReturn Map("foo" -> "bar", "hello" -> "world")
    when(s3.put(eqq("aws-frontend-backup"), any[String], putContentMatcher, eqq("arn:aws:kms:eu-west-1:642631414762:alias/FrontendConfigKey"))) thenReturn result

    val backupService = new BackupService(parameterStore, s3, Env("app", "stack", "stage"))

    backupService.backupParameterStore()

    verify(parameterStore).getPath("/frontend", isRecursiveSearch = true)
    verify(s3).put(eqq("aws-frontend-backup"), any[String], putContentMatcher, eqq("arn:aws:kms:eu-west-1:642631414762:alias/FrontendConfigKey"))
  }
}
