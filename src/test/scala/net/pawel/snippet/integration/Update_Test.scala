package net.pawel.snippet.integration

import net.pawel.services.Series_Service._
import net.pawel.model.{Episode, Series}
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import org.junit.{Before, Test}
import collection.immutable.List
import net.pawel.injection.Uses_Integration_Configuration
import base.Prepare_Orm
import net.pawel.snippet.Update
import com.novocode.junit.TestMarker

class Update_Test extends Uses_Integration_Configuration with Prepare_Orm with TestMarker {

  var series: Series = _

  @Before
  def create_test_series {
    series = find_series("Alternatywy 4").iterator.next
    create_series(series)
  }

  @Test
  def Update_Doesnt_Change_Anything_If_Online_Episodes_Are_The_Same_As_Local {
    val episodes_before_update: List[Episode] = series.episodes
    Update.update
    val episodes_after_update: List[Episode] = series.episodes
    assertThat(episodes_after_update, is(episodes_before_update))
  }

  @Test
  def Update_Restores_Missing_Episode {
    val episodes: List[Episode] = series.episodes
    val fifth_episode: Episode = series.episodes(4)
    fifth_episode.delete_!

    assertThat(series.episodes, is(episodes.filterNot(_ == fifth_episode)))

    Update.update

    val zipped: List[(Episode, Episode)] = episodes.zip(series.episodes)
    val significant_pair: (Episode, Episode) = zipped(4)
    val (expected_episode, actual_episode) = significant_pair
    val (expected, actual) = (zipped.filterNot(_ == significant_pair)).unzip
    assertThat(actual, is(expected))

    assertThat(actual_episode.series_id.toString, is(expected_episode.series_id.toString))
    assertThat(actual_episode.episode_id.toString, is(expected_episode.episode_id.toString))
    assertThat(actual_episode.name.toString, is(expected_episode.name.toString))
    assertThat(actual_episode.number.toString, is(expected_episode.number.toString))
    assertThat(actual_episode.overview.toString, is(expected_episode.overview.toString))
  }

  @Test
  def Update_Changes_Outdated_Episode {
    val fifth_episode: Episode = series.episodes(4)
    val last_updated = fifth_episode.last_updated.toString
    val name = fifth_episode.name.toString
    val aired = fifth_episode.aired.toString
    fifth_episode.last_updated(last_updated.toLong - 1)
    fifth_episode.name(name + "test")
    fifth_episode.aired(null)
    fifth_episode.save

    Update.update

    val episode_after_update: Episode = series.episodes(4)
    assertThat(episode_after_update.name.toString, is(name))
    assertThat(episode_after_update.last_updated.toString, is(last_updated))
    assertThat(episode_after_update.aired.toString, is(aired))
  }
}