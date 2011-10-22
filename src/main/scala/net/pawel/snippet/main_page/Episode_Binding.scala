package net.pawel.snippet.main_page

import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.pawel.model.Episode
import net.liftweb.http.js.JsCmds
import xml._
import net.pawel.snippet.Season_Link

trait Episode_Binding extends Season_Link{
  
  def bindEpisodesCss(episodes: List[Episode]) = episodes.map(episode =>
    ( ".season" #> episode.season
    & ".episode" #> episode.number
    & ".aired" #> episode.aired
    & ".id" #> episode.episode_id
    & ".name" #> attachOverview(a(Text(episode.name), JsCmds.Noop), episode.overview)
    & ".season" #> episode.season
    & ".series_name" #> season_link(episode.series, episode.season, episode.series.name)
    & ".overview" #> episode.overview
    & ".watched" #> ajaxCheckbox(episode.watched, watched => {
    episode.mark_watched(watched)
    JsCmds.RedirectTo("")
  })))

  def attachOverview(elem: Elem, overview: String) =
    (elem % ("onmouseover" -> ("tooltip.show('" + overview.replace("'", "\\'") + "')"))
         % ("onmouseout" -> "tooltip.hide()"))

}

