name := "plantmap-library"

// Beta version 1.1
version := "1.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
maintainer in Linux := "GreenMapper Team"
packageSummary in Linux := "PlantMap for Linux"
packageDescription := "PlantMap version 1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
libraryDependencies += "org.elasticsearch" % "elasticsearch" % "2.1.1"
libraryDependencies += "io.searchbox" % "jest" % "2.0.0"
libraryDependencies += "commons-io" % "commons-io" % "2.4"
libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5"

libraryDependencies += "org.imgscalr" % "imgscalr-lib" % "4.2"


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java  // Java project. Don't expect Scala IDE
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)  // Use .class files instead of generated .scala files for views and routes 

javaOptions in Test += "-Dconfig.file=conf/test.conf"