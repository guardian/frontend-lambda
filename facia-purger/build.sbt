import sbtassembly.Log4j2MergeStrategy

name := "facia-purger"

scalaVersion := "2.11.8"
val log4jVersion = "2.16.0"

organization := "com.gu"
description := "Lambda for purging Fastly cache based on s3 events"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "com.squareup.okhttp3" % "okhttp" % "3.2.0",
  "org.parboiled" %% "parboiled" % "2.1.3",
  "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
  "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
  "com.amazonaws" % "aws-lambda-java-log4j2" % "1.4.0",
  "org.scalatest" %% "scalatest" % "2.2.2" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
  )
  
def env(key: String): Option[String] = Option(System.getenv(key))

lazy val root = (project in file(".")).enablePlugins(RiffRaffArtifact)

enablePlugins(RiffRaffArtifact)

assemblyJarName := "facia-purger.jar"
riffRaffManifestProjectName := s"dotcom:lambda:${normalizedName.value}"
riffRaffArtifactResources += (baseDirectory.value / "cloudformation.yaml" -> "cloudformation/cloudformation.yaml")
riffRaffArtifactResources += (baseDirectory.value / "riff-raff.yaml" -> "riff-raff.yaml")

assembly / assemblyMergeStrategy := {
  case PathList(ps @ _*) if ps.last == "Log4j2Plugins.dat" => Log4j2MergeStrategy.plugincache
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _                             => MergeStrategy.deduplicate
}

riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
