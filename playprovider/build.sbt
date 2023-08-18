name := fromEnv("project.artifactId").get

organization := "com.mindscapehq"

version := fromEnv("project.version").get

scalaVersion := "2.13.11"

crossScalaVersions := Seq("2.12.18", "2.13.11")

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.mindscapehq" % "core" % fromEnv("project.parent.version").get,
  "com.typesafe.play" %% "play" % "2.8.20"
)

def fromEnv(name: String) = System.getenv(name) match {
    case null => None
    case value => Some(value)
}
