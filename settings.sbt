organization := "org.sazabi"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.5"

crossScalaVersions := Seq(scalaVersion.value, "2.10.4")

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.1-M5" exclude("org.scala-lang", "scala-compiler"))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.5" % "test")
