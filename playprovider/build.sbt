name := fromEnv("project.artifactId").getOrElse("raygun4java-play2")

organization := "com.mindscapehq"

version := fromEnv("project.version").getOrElse("0.4.6-SNAPSHOT")

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.10.4", "2.11.1", "2.11.2", "2.11.4")

resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.mindscapehq" % "core" % "1.3.1",
  "com.typesafe.play" %% "play" % play.core.PlayVersion.current
)

def fromEnv(name: String) = System.getenv(name) match {
    case null => None
    case value => Some(value)
}
