package com.gu.backupparameterstore

import com.gu.aws.{ ParameterStore, S3 }
import org.mockito.ArgumentMatchers.{ eq => eqq, _ }
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import software.amazon.awssdk.services.s3.model.PutObjectResponse

class BackupServiceTest extends AnyFlatSpec with MockitoSugar with Matchers {
  "backupParameterStore" should "find parameters from parameter store and put them in S3" in {
    val parameterStore = mock[ParameterStore]
    val s3 = mock[S3]
    val expectedPut =
      """
        |{
        |    "foo" : "bar",
        |    "hello" : "world"
        |}
      """.stripMargin

    def putContentMatcher: String = argThat[String](_.replaceAll("\\s", "") == expectedPut.replaceAll("\\s", ""))
    when(parameterStore.getPath("/frontend", isRecursiveSearch = true)) thenReturn Map("foo" -> "bar", "hello" -> "world")
    when(s3.put(eqq("aws-frontend-backup"), any[String], putContentMatcher, eqq("arn:aws:kms:eu-west-1:642631414762:alias/FrontendConfigKey"))) thenAnswer { invocation =>
      val args = invocation.getArguments
      PutObjectResponse.builder.build
    }

    val backupService = new BackupService(parameterStore, s3, Env("app", "stack", "stage"))

    backupService.backupParameterStore()

    verify(parameterStore).getPath("/frontend", isRecursiveSearch = true)
    verify(s3).put(eqq("aws-frontend-backup"), any[String], putContentMatcher, eqq("arn:aws:kms:eu-west-1:642631414762:alias/FrontendConfigKey"))
  }
}
