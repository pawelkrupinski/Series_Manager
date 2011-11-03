package net.pawel.comet

import net.pawel.snippet.Season_Link
import net.liftweb.http.js.JsCmds
import xml.{Elem, Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.pawel.lib.{Mark_Episode_Watched, Episode_Manager}
import net.pawel.model.{Series, Episode}
import com.google.common.collect.MapMaker
import com.google.common.base.Function
import java.util.concurrent.ConcurrentMap

trait Episode_Binding_Comet extends Season_Link {

  def bindEpisodesCss(episodes: List[Episode], userId: Long = 0) = {
    val lastEpisodes: ConcurrentMap[Series, Option[Episode]] = new MapMaker().makeComputingMap(
      new Function[Series, Option[Episode]] { def apply(series: Series) = series.last_watched_episode })

    episodes.map(episode =>
      ".season *" #> episode.season
        & ".episode *" #> episode.number
        & ".aired *" #> episode.aired
        & ".id *" #> episode.episode_id
        & ".name *" #> attachOverview(a(Text(episode.name), JsCmds.Noop), episode.overview)
        & ".season *" #> episode.season
        & ".series_name *" #> season_link(episode.series, episode.season, episode.series.name)
        & ".overview *" #> episode.overview
        & ".watched *" #> ajaxCheckbox(episode.watched(lastEpisodes.get(episode.series)),
        watched => Episode_Manager ! Mark_Episode_Watched(episode, userId)))
  }

  def attachOverview(elem: Elem, overview: String) =
    (elem % ("onmouseover" -> ("tooltip.show('" + overview.replace("'", "\\'") + "')"))
         % ("onmouseout" -> "tooltip.hide()"))
}