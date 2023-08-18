logLevel := Level.Info

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Workaround for version conflict: https://github.com/sbt/sbt/issues/7007#issuecomment-1221823661
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.20")
