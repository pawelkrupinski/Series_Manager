package net.pawel.comet

import net.pawel.snippet.Season_Link
import net.pawel.model.Episode
import net.liftweb.http.js.JsCmds
import xml.{Elem, Text}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.{CometListener, CometActor}
import net.pawel.lib.{Updated_Watched, Mark_Episode_Watched, Episode_Manager}

trait Episode_Binding_Comet extends Season_Link with CometListener {

  def bindEpisodesCss(episodes: List[Episode]) = episodes.map(episode =>
    ".season *" #> episode.season
    & ".episode *" #> episode.number
    & ".aired *" #> episode.aired
    & ".id *" #> episode.episode_id
    & ".name *" #> attachOverview(a(Text(episode.name), JsCmds.Noop), episode.overview)
    & ".season *" #> episode.season
    & ".series_name *" #> season_link(episode.series, episode.season, episode.series.name)
    & ".overview *" #> episode.overview
    & ".watched *" #> ajaxCheckbox(episode.watched,
      watched => Episode_Manager.is ! Mark_Episode_Watched(episode)))

  def attachOverview(elem: Elem, overview: String) =
    (elem % ("onmouseover" -> ("tooltip.show('" + overview.replace("'", "\\'") + "')"))
         % ("onmouseout" -> "tooltip.hide()"))

  protected def registerWith = Episode_Manager.is

  override def lowPriority = {
    case Updated_Watched(from, to) => reRender()
  }
}