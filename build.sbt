import AssemblyKeys._ // put this at the top of the file

seq(assemblySettings: _*)

name := "sfserver"

organization := "com.sparqlfiddle"

version := "0.1"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Scala-Tools Nexus Repository for Releases" at "http://nexus.scala-tools.org/content/repositories/releases",
  "Scala-Tools Maven Repository" at "http://www.scala-tools.org/repo-releases/"
)

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.8" % "test",
  "commons-configuration" % "commons-configuration" % "1.7",
  "org.clapper" %% "avsl" % "0.3.6",
  //"org.apache.httpcomponents" % "httpclient" % "4.1.2",
  //"com.h2database" % "h2" % "1.3.161",
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.0"
)

