name := "PortalInstitucional"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "net.sourceforge.htmlunit" % "htmlunit" % "2.14"   
)     

play.Project.playJavaSettings
