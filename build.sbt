import AddSettings._

val files = Seq(file("../settings.sbt"),
  file("../publish.sbt"))

lazy val root = project.in(file(".")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, defaultSbtFiles
).settings(
  publishArtifact := false
).aggregate(
  core,
  bijection,
  spire
)

lazy val core = project.in(file("core")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-core",
  libraryDependencies += "com.chuusai" %% "shapeless" % "2.1.0"
)

lazy val bijection = project.in(file("bijection")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-bijection",
  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.1.0",
    "com.twitter" %% "bijection-core" % "0.7.2")
)

lazy val spire = project.in(file("spire")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-spire",
  libraryDependencies += "org.spire-math" %% "spire" % "0.9.1"
).dependsOn(core)
