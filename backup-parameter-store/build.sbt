
name := "backup-parameter-store"

organization := "com.gu"

description:= "Backs up parameter store properties. To be executed on an interval"

version := "1.0"

scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-dead-code"
)

val awsVersion = "1.11.240"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
  "com.amazonaws" % "aws-java-sdk-core" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-ssm" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,
   "com.typesafe" % "config" % "1.3.1"
)

enablePlugins(RiffRaffArtifact)

assemblyJarName := s"${name.value}.jar"
riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffArtifactResources += (file("cfn.yaml"), s"cfn.yaml")