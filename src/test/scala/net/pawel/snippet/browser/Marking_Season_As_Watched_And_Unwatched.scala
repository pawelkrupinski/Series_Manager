package net.pawel.snippet.browser

import base.Start_Web_Server
import net.pawel.services.Series_Service
import com.google.inject.Inject
import org.openqa.selenium.{WebDriver, WebElement, By}
import org.junit.Assert._
import net.pawel.model.Implicits._
import org.hamcrest.CoreMatchers._
import scala.collection.JavaConversions._
import net.pawel.injection.{Uses_Offline_Configuration, Uses_Integration_Configuration, Injected}
import org.hamcrest.CoreMatchers._
import org.junit.{After, Test}
import net.pawel.model.Series

class Marking_Season_As_Watched_And_Unwatched extends Uses_Integration_Configuration with Start_Web_Server with Injected with Browser_Test {

  @Inject
  var driver: WebDriver = _

  val series_name = "Earth: Final Conflict"
  val series_id = "71784"

  @After
  def close_browser {
    driver.close
  }

  @Test
  def Marking_Season_As_Watched_Also_Marks_Previous_Seasons {
    Series_Service.create_series(Series_Service.find_series(series_name).head)

    driver.get("http://localhost:8081/");
    driver.findElement(By.xpath("//*[text()='List series']")).click
    driver.findElement(By.id(series_id)).click
    val links = driver.findElements(By.xpath("//*[contains(text(), 'Earth: Final Conflict Season')]"))

    driver.findElement(By.id(series_id + "_Season_3" )).click
    wait_for(() => (1 to 3).foreach(season => assertThat(driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(true))))
    (4 to 5).foreach(season => assertThat(driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(false)))
  }

  @Test
  def Marking_Season_As_Unwatched_Also_Marks_Next_Seasons {
    val series: Series = Series_Service.find_series(series_name).head
    Series_Service.create_series(series)
    series.season(5).sorted.last.mark_watched(true)
    assertTrue(series.episodes.forall(_.watched))

    driver.get("http://localhost:8081/");
    driver.findElement(By.xpath("//*[text()='List series']")).click
    driver.findElement(By.id(series_id)).click
    val links = driver.findElements(By.xpath("//*[contains(text(), 'Earth: Final Conflict Season')]"))

    driver.findElement(By.id(series_id + "_Season_3" )).click
    wait_for(() => (1 to 2).foreach(season => assertThat("Season " + season + " should be marked as watched.",
      driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(true))))
    wait_for(() => (3 to 5).foreach(season => assertThat("Season " + season + " should be marked as unwatched.",
      driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(false))))
  }

}