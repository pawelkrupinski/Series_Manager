package net.pawel.snippet

import java.util.Date
import main_page.{Episode_Binding, Episode_Fetching}
import net.pawel.model.Implicits._
import net.pawel.model.Episode
import net.liftweb.util.Helpers._
import util._
import net.liftweb._
import http.js.JsCmds
import http.SHtml._
import util.BindHelpers._
import util.CssSel
import xml.Text._
import xml.{Text, Elem, NodeSeq}
import net.pawel.snippet.List_Seasons._
import org.joda.time.DateTime

class Recently_Aired extends Episode_Fetching with Episode_Binding {
  def render = ".recently_aired *" #> bind(recently_aired_episodes)

  def bind(episodes: List[Episode]) = episodes.map(episode =>
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

  override def attachOverview(elem: Elem, overview: String) =
    (elem % ("onmouseover" -> ("tooltip.show('" + overview.replace("'", "\\'") + "')"))
         % ("onmouseout" -> "tooltip.hide()"))

}