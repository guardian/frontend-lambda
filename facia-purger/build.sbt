import sbtassembly.Log4j2MergeStrategy

name := "facia-purger"

scalaVersion := "2.13.10"
val log4jVersion = "2.17.1"

organization := "com.gu"
description := "Lambda for purging Fastly cache based on s3 events"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
  "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
  "com.squareup.okhttp3" % "okhttp" % "4.9.2",
  "org.parboiled" %% "parboiled" % "2.4.1",
  "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
  "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
  "com.amazonaws" % "aws-lambda-java-log4j2" % "1.5.1",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
  )
  
def env(key: String): Option[String] = Option(System.getenv(key))

lazy val root = (project in file(".")).enablePlugins(RiffRaffArtifact)

assemblyJarName := "facia-purger.jar"
riffRaffManifestProjectName := s"dotcom:lambda:${normalizedName.value}"
riffRaffArtifactResources += (baseDirectory.value / "../cdk/cdk.out/FaciaPurger-CODE.template.json" -> "cloudformation/cloudformation-CODE.yaml")
riffRaffArtifactResources += (baseDirectory.value / "../cdk/cdk.out/FaciaPurger-PROD.template.json" -> "cloudformation/cloudformation-PROD.yaml")
riffRaffArtifactResources += (baseDirectory.value / "riff-raff.yaml" -> "riff-raff.yaml")

assembly / assemblyMergeStrategy := {
  case PathList(ps @ _*) if ps.last == "Log4j2Plugins.dat" => Log4j2MergeStrategy.plugincache
  // https://stackoverflow.com/a/55557287
  // Okhttp and log4j both have module-info files, but we don't actually need either file.
  case PathList(ps @ _*) if ps.last == "module-info.class" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
