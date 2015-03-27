organization := "org.sazabi"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.11.6"

crossScalaVersions := Seq(scalaVersion.value, "2.10.5")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.1-M5" exclude("org.scala-lang", "scala-compiler"))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test")
