name := """iskoristi-dan"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.12",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.0.4",
  "commons-io" % "commons-io" % "2.4",
  "com.jayway.restassured" % "rest-assured" % "1.7" % "test"
)