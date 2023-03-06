
name := "backup-parameter-store"

organization := "com.gu"

description:= "Backs up parameter store properties. To be executed on an interval"

scalaVersion := "2.13.10"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-release:8",
  "-Ywarn-dead-code"
)

val awsVersion = "2.20.12"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "software.amazon.awssdk" % "lambda" % awsVersion,
  "software.amazon.awssdk" % "sdk-core" % awsVersion,
  "software.amazon.awssdk" % "ssm" % awsVersion,
  "software.amazon.awssdk" % "s3" % awsVersion,
  "software.amazon.awssdk" % "url-connection-client" % awsVersion,
  "com.amazonaws" % "aws-lambda-java-events" % "3.11.0",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalactic" %% "scalactic" % "3.2.15",
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % "test",
  "org.mockito" % "mockito-core" % "2.13.0" % Test,
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.9.0",
)


enablePlugins(RiffRaffArtifact)

assemblyJarName := s"${name.value}.jar"
riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffArtifactResources += (file("cfn.yaml"), s"${name.value}-cfn/cfn.yaml")
riffRaffManifestProjectName := s"dotcom:${name.value}"



