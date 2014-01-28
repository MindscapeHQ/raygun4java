libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)

play.Project.playJavaSettings

def fromEnv(name: String) = System.getenv(name) match {
    case null => None
    case value => Some(value)
}

val appName = fromEnv("project.artifactId").getOrElse("my-app")

val appVersion = fromEnv("project.version").getOrElse("1.0-SNAPSHOT")

