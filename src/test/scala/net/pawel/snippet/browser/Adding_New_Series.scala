package net.pawel.snippet.browser

import org.openqa.selenium.{By, WebElement}
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import org.hamcrest.Matcher
import org.junit.Test
import net.pawel.services.Series_Service
import scala.collection.JavaConversions._
import net.pawel.injection.{Injected, Uses_Integration_Configuration}
import base.{Prepare_Orm, Start_Web_Server}

class Adding_New_Series extends Uses_Integration_Configuration
                        with Injected
                        with Browser_Test
                        with Start_Web_Server {

  @Test
  def If_Series_Is_Already_Added_Does_Not_Show_Link_In_Search_Results {
    Series_Service.create_series(Series_Service.find_series("Earth: Final Conflict").head)

    driver.get(server_config.url);
    driver.findElement(By.xpath("//*[text()='Find series']")).click
    val series_name: WebElement = wait_for(() => driver.findElement(By.id("series_name")))
    series_name.click
    series_name.sendKeys("Earth: Final Conflict")
    driver.findElement(By.id("series_search_submit")).click

    wait_for(() => driver.findElement(By.xpath("//*[text()='Series found for query \"Earth: Final Conflict\"']")))
    assertThat(driver.findElements(By.id("71784")).size, is(0))
  }

  val exists: Matcher[WebElement] = is(not(nullValue[WebElement]))

  @Test
  def Adding_New_Series {
    driver.get(server_config.url);
    driver.findElement(By.xpath("//*[text()='Find series']")).click
    val series_name: WebElement = driver.findElement(By.id("series_name"))
    series_name.click
    series_name.sendKeys("Earth: Final Conflict")

    driver.findElement(By.id("series_search_submit")).click
    wait_for(() => driver.findElement(By.id("71784"))).click
    wait_for(() => driver.findElement(By.xpath("//*[text()='Series list']")))
    driver.findElement(By.id("71784")).click
    wait_for(() => driver.findElements(By.xpath("//*[contains(text(), 'Earth: Final Conflict Season')]")))
    val season_links = driver.findElements(By.xpath("//*[contains(text(), 'Earth: Final Conflict Season')]"))

    assertThat(season_links.map(_.getText).toList, is((1 to 5).map("Earth: Final Conflict Season " + _).toList))
  }
}