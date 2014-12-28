name := """iskoristi-dan"""

version := "0.9"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.12",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.0.4",
  "commons-io" % "commons-io" % "2.4"
)