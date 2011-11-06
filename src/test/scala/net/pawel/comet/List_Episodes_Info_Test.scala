package net.pawel.comet

import org.junit.Test
import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit.{Assert, Test}
import net.pawel.lib.Updated_Watched
import net.liftweb.common.Full
import net.pawel.model.{Series, Episode}

class List_Episodes_Info_Test {

  var user_id = 3
  var series_id = 2
  var season_number = 1

  val series = new Series()
  series.series_id(series_id)

  private val info = new List_Episodes_Info(user_id, series, season_number)

  @Test
  def overlapsTest() {
    assertTrue(info.overlaps(Updated_Watched(episode(series_id, season_number, 3), episode(series_id, season_number, 5))))
    assertTrue(info.overlaps(Updated_Watched(episode(series_id, season_number, 5), episode(series_id, season_number, 3))))
    assertTrue(info.overlaps(Updated_Watched(episode(series_id, season_number-1, 10), episode(series_id, season_number, 1))))
    assertTrue(info.overlaps(Updated_Watched(episode(series_id, season_number-1, 10), episode(series_id, season_number+1, 1))))
    assertTrue(info.overlaps(Updated_Watched(episode(series_id, season_number, 10), episode(series_id, season_number+1, 1))))

    assertFalse(info.overlaps(Updated_Watched(episode(666, season_number, 1), episode(666, season_number, 2))))
    assertFalse(info.overlaps(Updated_Watched(episode(series_id, season_number-1, 10), episode(series_id, season_number-1, 11))))
    assertFalse(info.overlaps(Updated_Watched(episode(series_id, season_number-1, 11), episode(series_id, season_number-1, 10))))
    assertFalse(info.overlaps(Updated_Watched(episode(series_id, season_number+1, 10), episode(series_id, season_number+1, 11))))
    assertFalse(info.overlaps(Updated_Watched(episode(series_id, season_number+1, 11), episode(series_id, season_number+1, 10))))
  }

  def episode(series: Long, season: Int, episode_number: Int) = Some(new Episode().series_id(series).season(season).number(episode_number))
}