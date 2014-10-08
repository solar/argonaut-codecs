import AddSettings._

val files = Seq(file("../settings.sbt"),
  file("../publish.sbt"))

lazy val root = project.in(file(".")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, defaultSbtFiles
).settings(
  packagedArtifacts := Map.empty
).aggregate(
  core,
  bijection
)

lazy val core = project.in(file("core")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-core"
)

lazy val bijection = project.in(file("bijection")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-bijection",
  libraryDependencies += "com.twitter" %% "bijection-core" % "0.7.2"
)
