import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) with IdeaProject {
  val liftVersion = property[Version]

  val repo = "Guice" at "http://guice-maven.googlecode.com/svn/trunk"
  val selenium = "Selenium" at "http://repo1.maven.org/maven2"
  val christoph = "Christoph's Maven Repo" at "http://maven.henkelmann.eu/"

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion.value.toString % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion.value.toString % "compile",
    "org.mortbay.jetty" % "jetty" % "6.1.26" % "test",
    "junit" % "junit" % "4.7" % "test",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "org.scala-tools.testing" %% "specs" % "1.6.8" % "test",
    "com.h2database" % "h2" % "1.2.147",
    "com.google.guava" % "guava" % "r09",
//    "org.squeryl" % "squeryl_2.8.1" % "0.9.4-RC3",
    "com.google.code.guice" % "guice" % "2.0.1",
    "aopalliance" % "aopalliance" % "1.0",
    "org.seleniumhq.selenium" % "selenium-java" % "2.8.0",// withSources,
    "mysql" % "mysql-connector-java" % "5.1.13",
    "com.novocode" % "junit-interface" % "0.5" % "test"
  ) ++ super.libraryDependencies

  override def testOptions =
    super.testOptions ++
    Seq(TestArgument(TestFrameworks.JUnit, "-q", "-v"))
}
