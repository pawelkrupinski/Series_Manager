package net.pawel.comet

import net.pawel.model.Episode
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import xml._
import net.liftweb.http.CometActor
import net.pawel.snippet.Season_Link

trait Episode_Binding_Comet extends CometActor with Season_Link {

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
      watched => this ! Mark_Episode_Watched(episode)))

  def attachOverview(elem: Elem, overview: String) =
    (elem % ("onmouseover" -> ("tooltip.show('" + overview.replace("'", "\\'") + "')"))
         % ("onmouseout" -> "tooltip.hide()"))

  case class Mark_Episode_Watched(episode: Episode)

  override def lowPriority = {
    case Mark_Episode_Watched(episode) => episode.mark_watched(); reRender()
  }
}