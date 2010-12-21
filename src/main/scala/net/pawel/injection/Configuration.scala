package net.pawel.injection

import com.google.inject.{Module, AbstractModule, Guice, Injector}
import net.pawel.services.{Offline_Http, Http}
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import net.pawel.model.Implicits._
import java.io.File
import org.openqa.selenium.firefox.{FirefoxProfile, FirefoxBinary, FirefoxDriver}
import org.openqa.selenium.chrome.ChromeDriver

class Database_Connection_Settings(val driver: String, val url: String, val user: String, val password: String)

trait Uses_Integration_Configuration {
  Configuration.use_integration_configuration
}

trait Uses_Offline_Configuration {
  Configuration.use_offline_configuration
}

object Settings {
  val prod_database_settings = new Database_Connection_Settings("com.mysql.jdbc.Driver",
    "jdbc:mysql://ec2-174-129-9-255.compute-1.amazonaws.com:3306/series", "series", "series")
  val local_file_database_settings = new Database_Connection_Settings("org.h2.Driver",
    "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE", null, null)
  val in_memory_database_settings = new Database_Connection_Settings("org.h2.Driver", "jdbc:h2:mem:", null, null)
}

private class Test_Configuration extends AbstractModule {
  override protected def configure {
    bind(classOf[Database_Connection_Settings]).toInstance(Settings.in_memory_database_settings)
    bind(classOf[WebDriver]).to_provider(Firefox_Driver _)
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
    bind(classOf[Database_Connection_Settings]).toInstance(Settings.prod_database_settings)
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
  var module: Module = new Production_Configuration

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