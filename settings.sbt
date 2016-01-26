organization := "org.sazabi"

crossScalaVersions := Seq("2.11.7", "2.10.6")

scalaVersion := crossScalaVersions.value.head

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

libraryDependencies += "io.argonaut" %% "argonaut" % "6.1"

libraryDependencies += "com.github.scalaprops" %% "scalaprops" % "0.1.16" % "test"

testFrameworks += new TestFramework("scalaprops.ScalapropsFramework")

parallelExecution in Global := false
