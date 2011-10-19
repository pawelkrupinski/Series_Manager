package net.pawel.snippet.browser

import net.pawel.services.Series_Service
import org.openqa.selenium.By
import org.junit.Assert._
import net.pawel.model.Implicits._
import org.hamcrest.CoreMatchers._
import org.junit.Test
import net.pawel.model.Series
import net.pawel.injection.{Uses_Integration_Configuration, Injected}
import base.Start_Web_Server

class Marking_Season_As_Watched_And_Unwatched extends Uses_Integration_Configuration
                                              with Start_Web_Server
                                              with Injected
                                              with Browser_Test {

  val series_name = "Earth: Final Conflict"
  val series_id = "71784"

  @Test
  def Marking_Season_As_Watched_Also_Marks_Previous_Seasons {
    Series_Service.create_series(Series_Service.find_series(series_name).head)

    driver.get(server_config.url);
    driver.findElement(By.xpath("//*[text()='List series']")).click
    wait_for(() => driver.findElement(By.id(series_id))).click

    wait_for(() => driver.findElement(By.id(series_id + "_Season_3" ))).click
    (1 to 3).foreach(season => wait_for(() => assertThat(driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(true))))
    (4 to 5).foreach(season => wait_for(() => assertThat(driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(false))))
  }

  @Test
  def Marking_Season_As_Unwatched_Also_Marks_Next_Seasons {
    val series: Series = Series_Service.find_series(series_name).head
    Series_Service.create_series(series)
    series.season(5).sorted.last.mark_watched(true)
    assertTrue(series.episodes.forall(_.watched))

    driver.get(server_config.url);
    driver.findElement(By.xpath("//*[text()='List series']")).click
    driver.findElement(By.id(series_id)).click

    driver.findElement(By.id(series_id + "_Season_3" )).click
    (1 to 2).foreach(season => wait_for(() => assertThat("Season " + season + " should be marked as watched.",
      driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(true))))
    (3 to 5).foreach(season => wait_for(() => assertThat("Season " + season + " should be marked as unwatched.",
      driver.findElement(By.id(series_id + "_Season_" + season)).isSelected, is(false))))
  }

}