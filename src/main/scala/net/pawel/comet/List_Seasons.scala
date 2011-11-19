package net.pawel.comet

import net.liftweb.http.SHtml._
import java.lang.String
import xml._
import net.pawel.model.{User, Episode}
import net.pawel.snippet.Season_Link
import net.pawel.lib._
import net.pawel.model.Series
import net.liftweb.http.CometActor
import net.pawel.lib.{Remove_Listener, Add_Listener, Episode_Manager, Updated_Watched}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds
import net.liftweb.common.Logger

class List_Seasons extends CometActor with Season_Link with Logger {

  var series_id: Long = _
  var user_id: Long = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    series_id = params(1).toLong
    user_id = params(0).toLong
    Episode_Manager ! Add_Listener(this, user_id)
  }

  override protected def localShutdown() {
    super.localShutdown()
    Episode_Manager ! Remove_Listener(this, user_id)
  }

  def render = {
    val series = Series.find_by_id(series_id).open_!
    val season_numbers: List[Int] = series.episodes.map(_.season.toString.toInt).toSet.toList.sorted
    val last_watched_episode = series.last_watched_episode

    def bindEpisodes = season_numbers.map(season_number =>
      {
        debug("Rendering season " + season_number)
        val season: List[Episode] = series.season(season_number).sorted
        debug("Episodes of season " + season_number + ": " + season)

        def episode_to_mark(watched: Boolean) = if (watched) season.last else season.head

        debug("Last watched episode of series: " + last_watched_episode)

        val last_episode_watched: Boolean = season.last.watched(last_watched_episode)
        debug("Last episode of season " + season_number + " watched: " + last_episode_watched)

        val checkbox: Elem = ajaxCheckbox(last_episode_watched, watched => {
          Episode_Manager ! Mark_Episode_Watched(episode_to_mark(watched), user_id);
          JsCmds.Noop
        })
        ".seasonLink *" #> season_link(series, season_number) &
        ".watched *" #> (checkbox % ("id" -> (series.id.get.toString + "_Season_" + season_number)))
      }
      )

      ".seriesName" #> series.name &
      "#seasons *" #> bindEpisodes
  }

  def season_link(series: Series, season_number: Int): NodeSeq = {
    val text: String = series.name + " Season " + season_number
    season_link(series, season_number, text)
  }

  override def lowPriority = {
    case u: Updated_Watched => reRender();
  }
}

