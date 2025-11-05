
name := "backup-parameter-store"

organization := "com.gu"

description:= "Backs up parameter store properties. To be executed on an interval"

scalaVersion := "2.13.17"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-release:11",
  "-Ywarn-dead-code"
)

initialize := {
  val _ = initialize.value
  assert(sys.props("java.specification.version") == "11",
    "Java 11 is required for this project.")
}

val awsVersion = "2.37.3"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.3.14",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "com.amazonaws" % "aws-lambda-java-core" % "1.4.0",
  "software.amazon.awssdk" % "sdk-core" % awsVersion,
  "software.amazon.awssdk" % "ssm" % awsVersion,
  "software.amazon.awssdk" % "s3" % awsVersion,
  "com.amazonaws" % "aws-lambda-java-events" % "2.0.1",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalactic" %% "scalactic" % "3.2.15",
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % "test",
  "org.mockito" % "mockito-core" % "2.13.0" % Test
)

assemblyJarName := s"${name.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x => MergeStrategy.first
}