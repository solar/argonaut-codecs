organization := "org.sazabi"

version := "0.0.4-SNAPSHOT"

scalaVersion := "2.11.7"

crossScalaVersions := Seq(scalaVersion.value, "2.10.5")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

libraryDependencies += "io.argonaut" %% "argonaut" % "6.1"

libraryDependencies += "com.github.scalaprops" %% "scalaprops" % "0.1.11" % "test"

testFrameworks += new TestFramework("scalaprops.ScalapropsFramework")
parallelExecution in Test := false
