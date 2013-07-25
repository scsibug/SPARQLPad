import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForJarSettings: _*)

name := "sfserver"

organization := "com.sparqlpad"

version := "0.1"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Scala-Tools Nexus Repository for Releases" at "http://nexus.scala-tools.org/content/repositories/releases",
  "Scala-Tools Maven Repository" at "http://www.scala-tools.org/repo-releases/",
  "Restlet Maven Repository" at "http://maven.restlet.org/"
)

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.8" % "test",
  "commons-configuration" % "commons-configuration" % "1.7",
  "org.clapper" %% "avsl" % "0.3.6",
  //"org.apache.httpcomponents" % "httpclient" % "4.1.2",
  //"com.h2database" % "h2" % "1.3.161",
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.0",
  // Restlet
  "org.restlet.jse" % "org.restlet" % "2.0.11",
  "org.restlet.jse" % "org.restlet.ext.jackson" % "2.0.11",
  "org.restlet.jse" % "org.restlet.ext.slf4j"  % "2.0.11",
  "org.restlet.jse" % "org.restlet.ext.jetty"  % "2.0.11",
  // Jena (exclude log4j bridge, since we have avsl instead)
  //"com.hp.hpl.jena" % "jena" % "2.9.3" exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.jena" % "jena-arq" % "2.9.3" exclude("org.slf4j", "slf4j-log4j12")
)


