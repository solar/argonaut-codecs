organization := "org.sazabi"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint",
  "-Ybackend:GenBCode", "-Ydelambdafy:method", "-target:jvm-1.8")

libraryDependencies ++= Seq("io.argonaut" %% "argonaut" % "6.1",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.7.0")

libraryDependencies += "com.github.scalaprops" %% "scalaprops" % "0.1.16" % "test"

testFrameworks += new TestFramework("scalaprops.ScalapropsFramework")

parallelExecution in Global := false

incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(true)
