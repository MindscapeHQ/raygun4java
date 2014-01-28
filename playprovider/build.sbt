libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.mindscapehq" % "core" % "1.3.1"
)

def fromEnv(name: String) = System.getenv(name) match {
    case null => None
    case value => Some(value)
}

val appName = fromEnv("project.artifactId").getOrElse("my-app")

val appVersion = fromEnv("project.version").getOrElse("1.0-SNAPSHOT")

