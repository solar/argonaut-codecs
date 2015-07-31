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
    "com.google.guava" % "guava" % "18.0"),
  libraryDependencies ++= (
    if (scalaVersion.value.startsWith("2.10"))
      Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))
    else Seq()
  )
)

lazy val bijection = project.in(file("bijection")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-bijection",
  libraryDependencies ++= Seq(
    "com.chuusai" %% "shapeless" % "2.2.5",
    "com.twitter" %% "bijection-core" % "0.8.1"),
  libraryDependencies ++= (
    if (scalaVersion.value.startsWith("2.10"))
      Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))
    else Seq()
  )
)

lazy val spire = project.in(file("spire")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings(
  name := "argonaut-codecs-spire",
  libraryDependencies += "org.spire-math" %% "spire" % "0.10.1"
).dependsOn(core)
