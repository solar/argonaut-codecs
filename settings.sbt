organization := "org.sazabi"

version := "0.0.3-SNAPSHOT"

scalaVersion := "2.11.6"

crossScalaVersions := Seq(scalaVersion.value, "2.10.5")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

libraryDependencies += "io.argonaut" %% "argonaut" % "6.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test")
