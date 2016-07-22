name := "facia-purger"

version := "1.0.0"

scalaVersion := "2.11.8"

organization := "com.gu"
description := "Lambda for purging Fastly cache based on s3 events"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "com.squareup.okhttp3" % "okhttp" % "3.2.0",
  "org.parboiled" %% "parboiled" % "2.1.3",
  "log4j" % "log4j" % "1.2.17",
  "com.amazonaws" % "aws-lambda-java-log4j" % "1.0.0",
  "org.scalatest" %% "scalatest" % "2.2.2" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

enablePlugins(RiffRaffArtifact, UniversalPlugin)

packageName in Universal := normalizedName.value
topLevelDirectory in Universal := Some(normalizedName.value)

riffRaffPackageType := (packageBin in Universal).value
def env(key: String): Option[String] = Option(System.getenv(key))
riffRaffBuildIdentifier := env("TRAVIS_BUILD_NUMBER").getOrElse("DEV")
riffRaffManifestBranch := env("TRAVIS_BRANCH").getOrElse(git.gitCurrentBranch.value)
riffRaffManifestProjectName := "dotcom:lambda:facia-purger"

riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")