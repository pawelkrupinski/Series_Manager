package net.pawel.snippet.integration

import net.pawel.services.Series_Service._
import net.pawel.model.{Episode, Series}
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import collection.immutable.List
import base.Prepare_Orm
import net.pawel.snippet.Update
import com.novocode.junit.TestMarker
import net.pawel.injection.Uses_Offline_Configuration
import net.liftweb.actor.LiftActor
import bootstrap.liftweb.Boot
import scala.Some
import org.junit.{After, Before, Test}

class Update_Test extends Uses_Offline_Configuration with Prepare_Orm with TestMarker {

  val series_id: Long = create_series(find_series("Earth: Final Conflict").iterator.next).series_id.get
  def series: Series = Series.find_by_id(series_id).open_!

  @After
  def destroy_schema() {
    series.delete
  }

  def update {
    val waiter = new LiftActor {
      var waiting = true
      protected def messageHandler = {
        case Update.Updated => waiting = false;
      }
      def waitForIt {
        while(waiting) {
          Thread.sleep(100)
        }
      }
    }
    Update.update(waiter)
    waiter.waitForIt
  }

  @Test
  def Update_Doesnt_Change_Anything_If_Online_Episodes_Are_The_Same_As_Local {
    val episodes_before_update: List[Episode] = series_episodes
    update
    val episodes_after_update: List[Episode] = series_episodes
    assertThat(episodes_after_update, is(episodes_before_update))
  }

  @Test
  def Update_Restores_Missing_Episode {
    val episodes: List[Episode] = series_episodes
    val fifth_episode: Episode = episodes(4)
    fifth_episode.delete_!

    assertThat(series_episodes, is(episodes.filterNot(_ == fifth_episode)))

    update

    val zipped: List[(Episode, Episode)] = episodes.zip(series_episodes)
    val significant_pair: (Episode, Episode) = zipped(4)

    val (expected_episode, actual_episode) = significant_pair
    val excluded_pair: List[(Episode, Episode)] = zipped.filterNot(_ == significant_pair)
    excluded_pair.foreach(pair => {
      val (expected, actual) = pair
      areEqual(actual, expected)
    })

    areEqual(actual_episode, expected_episode)
  }

  def mark_last_watched(episode: Episode) {
    val local_series = series
    local_series.mark_last_watched(episode)
    println("Saved: " + local_series.save())
  }

  @Test
  def Updating_Last_Watched_Episode {
    val episodes: List[Episode] = series_episodes
    val fifth_episode: Episode = episodes(4)
    mark_last_watched(fifth_episode)

    fifth_episode.last_updated(fifth_episode.last_updated.get - 1)
    fifth_episode.save()

    val last_watched_episode: Option[Episode] = series.last_watched_episode
    assertThat(last_watched_episode, is(Option(fifth_episode)))

    update

    areEqual(series.last_watched_episode.get, fifth_episode)
  }

  @Test
  def Update_Changes_Outdated_Episode {
    val fifth_episode: Episode = series_episodes(4)
    val last_updated = fifth_episode.last_updated.toString
    val name = fifth_episode.name.toString
    val aired = fifth_episode.aired.toString
    fifth_episode.last_updated(last_updated.toLong - 1)
    fifth_episode.name(name + "test")
    fifth_episode.aired(null)
    fifth_episode.save

    update

    val episode_after_update: Episode = series_episodes(4)
    assertThat(episode_after_update.name.toString, is(name))
    assertThat(episode_after_update.last_updated.toString, is(last_updated))
    assertThat(episode_after_update.aired.toString, is(aired))
  }

  def series_episodes: List[Episode] = {
    series.episodes.sorted
  }

  def areEqual(actual_episode: Episode, expected_episode: Episode) {
    assertThat(actual_episode.series_id.toString, is(expected_episode.series_id.toString))
    assertThat(actual_episode.episode_id.toString, is(expected_episode.episode_id.toString))
    assertThat(actual_episode.name.toString, is(expected_episode.name.toString))
    assertThat(actual_episode.number.toString, is(expected_episode.number.toString))
    assertThat(actual_episode.overview.toString, is(expected_episode.overview.toString))
  }
}