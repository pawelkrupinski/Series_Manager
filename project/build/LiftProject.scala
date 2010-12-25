import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val liftVersion = "2.1"

  // uncomment the following if you want to use the snapshot repo
  //val scalatoolsSnapshot = ScalaToolsSnapshots

  // If you're using JRebel for Lift development, uncomment
  // this line
  // override def scanDirectories = Nil

  val repo = "Guice" at "http://guice-maven.googlecode.com/svn/trunk"
  val selenium = "Selenium" at "http://selenium.googlecode.com/svn/repository"
  val christoph = "Christoph's Maven Repo" at "http://maven.henkelmann.eu/"

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
    "junit" % "junit" % "4.5" % "test->default",
    "org.scala-tools.testing" %% "specs" % "1.6.5" % "test->default",
    "com.h2database" % "h2" % "1.2.138",
    "com.google.guava" % "guava" % "r07",
    "org.squeryl" % "squeryl_2.8.1" % "0.9.4-RC3",
    "com.google.code.guice" % "guice" % "2.0.1",
    "aopalliance" % "aopalliance" % "1.0",
    "org.seleniumhq.selenium" % "selenium" % "2.0a7" withSources,
    "org.seleniumhq.selenium" % "selenium-common" % "2.0a7" withSources,
    "mysql" % "mysql-connector-java" % "5.1.13",
    "com.novocode" % "junit-interface" % "0.5" % "test->default"
  ) ++ super.libraryDependencies

  override def testOptions =
    super.testOptions ++
    Seq(TestArgument(TestFrameworks.JUnit, "-q", "-v"))
}
