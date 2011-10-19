package net.pawel.injection

import net.pawel.services.{Offline_Http, Http}
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import net.pawel.model.Implicits._
import org.openqa.selenium.firefox.{FirefoxProfile, FirefoxBinary, FirefoxDriver}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import net.liftweb.util.Props
import java.io.{FileInputStream, File}
import net.liftweb.common.{Box, Full}
import com.google.inject._

class Database_Connection_Settings(val driver: String, val url: String, val user: Box[String], val password: Box[String])

case class Server_Config(val port: Int) {
  val url = "http://localhost:%s/".format(port.toString)
}

trait Uses_Integration_Configuration {
  Configuration.use_integration_configuration
}

trait Uses_Offline_Configuration {
  Configuration.use_offline_configuration
}

object Settings {
  def setProps(fileName: String) {
    Props.whereToLook = () => List(("local", () => Full(classOf[Database_Connection_Settings].getResourceAsStream(fileName))))
  }

  lazy val database_settings = new Database_Connection_Settings(Props.get("db.driver.class").openTheBox,
    Props.get("db.url").openTheBox, Props.get("db.username"), Props.get("db.password"))
}

private class Test_Configuration extends AbstractModule {

  def getProperty(key: String) = Option(System.getProperty(key))

  def debug[T](message: String, value: T): T = {
    println(String.format(message, value.asInstanceOf[Object]))
    value
  }

  override protected def configure {
    Settings.setProps("/props/test.props")
    bind(classOf[Database_Connection_Settings]).toInstance(Settings.database_settings)
    bind(classOf[WebDriver]).to_provider(browser_provider)
    bind(classOf[Server_Config]).toInstance(debug("Server config: %s", server_config))
  }

  val server_config = Server_Config(getProperty("serverPort").map(_.toInt).getOrElse(8081))

  val browser_provider: (() => WebDriver) = debug("Browser: %s", System.getProperty("browser")) match {
    case "firefox" => Firefox_Driver _
    case "ie" => () => new InternetExplorerDriver
    case "chrome" => () => new ChromeDriver
    case "htmlunit" => Html_Unit_Driver _
    case _ => Html_Unit_Driver _
  }

  def Firefox_Driver = new FirefoxDriver(
    new FirefoxBinary(new File("D:\\Programy\\Mozilla Firefox\\firefox.exe")),
    new FirefoxProfile())

  def Html_Unit_Driver = {
    val htmlUnitDriver: HtmlUnitDriver = new HtmlUnitDriver
    htmlUnitDriver.setJavascriptEnabled(true)
    htmlUnitDriver
  }
}

private class Production_Configuration extends AbstractModule {
  override protected def configure {
    Settings.setProps("/props/prod.props")
    bind(classOf[Database_Connection_Settings]).toInstance(Settings.database_settings)
    bind(classOf[Http]).toInstance(Http)
  }
}

private class Local_Configuration extends AbstractModule {
  override protected def configure {
    Settings.setProps("/props/local.props")
    bind(classOf[Database_Connection_Settings]).toInstance(Settings.database_settings)
    bind(classOf[Http]).toInstance(Http)
  }
}

private class Integration_Configuration extends Test_Configuration {
  override protected def configure {
    super.configure
    bind(classOf[Http]).toInstance(Http)
  }
}

private class Offline_Configuration extends Test_Configuration {
  override protected def configure {
    super.configure
    bind(classOf[Http]).toInstance(Offline_Http)
  }
}

object Configuration {
  var module: Module = new Local_Configuration

  def use_integration_configuration {
    module = new Integration_Configuration
  }

  def use_offline_configuration {
    module = new Offline_Configuration
  }

  lazy val injector: Injector = Guice.createInjector(module)
}

trait Injected {
  Configuration.injector.injectMembers(this)
}