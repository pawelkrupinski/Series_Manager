package base

import org.openqa.selenium.{By, WebElement, WebDriver}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.firefox.{FirefoxProfile, FirefoxDriver}
import actors.Actor
import net.pawel.injection.Integration_Configuration
import net.pawel.services.Series_Service
import org.junit.{Before, After, Assert, Test}
import bootstrap.liftweb.Boot
import net.RunWebApp

trait Start_Web_Server {
  @Before
  def start_server {
    RunWebApp.start_server
    Boot.schemify
  }

  @After
  def stop_server {
    Boot.destroy
  }
}