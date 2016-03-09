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
  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.2.5",
    "com.google.guava" % "guava" % "19.0")
)

lazy val bijection = project.in(file("bijection")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-bijection",
  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.2.5",
    "com.twitter" %% "bijection-core" % "0.8.1")
)

lazy val spire = project.in(file("spire")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-spire",
  libraryDependencies += "org.spire-math" %% "spire" % "0.11.0"
).dependsOn(core)
