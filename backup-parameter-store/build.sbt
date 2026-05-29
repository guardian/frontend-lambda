
name := "backup-parameter-store"

organization := "com.gu"

description:= "Backs up parameter store properties. To be executed on an interval"

scalaVersion := "2.13.18"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-release:11",
  "-Ywarn-dead-code"
)

val awsVersion = "2.42.41"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "2.0.18",
  "ch.qos.logback" % "logback-classic" % "1.5.32",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.6",
  "com.amazonaws" % "aws-lambda-java-core" % "1.4.0",
  "software.amazon.awssdk" % "sdk-core" % awsVersion,
  "software.amazon.awssdk" % "ssm" % awsVersion,
  "software.amazon.awssdk" % "s3" % awsVersion,
  "com.amazonaws" % "aws-lambda-java-events" % "2.0.2",
  "com.typesafe" % "config" % "1.3.4",
  "org.scalactic" %% "scalactic" % "3.2.20",
  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % "test",
  "org.mockito" % "mockito-core" % "5.23.0" % Test,
  "org.mockito" % "mockito-junit-jupiter" % "5.23.0" % Test
)

assemblyJarName := s"${name.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x => MergeStrategy.first
}