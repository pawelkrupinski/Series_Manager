package net.pawel.snippet

import net.pawel.model.{Series, Episode}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import java.lang.String
import xml.{Elem, NodeSeq}
import net.liftweb.http.{SessionVar, S}

class List_Seasons extends Season_Link{

  def render = {
    val series_id: Long = S.param("series_id").open_!.toLong
    val series: Series = Series.find_by_id(series_id).open_!
    val seasons: List[Int] = Episode.find_by_series_id(series_id).map(_.season.toString.toInt).toSet.toList.sorted

    def bindEpisodes = seasons.map(season_number =>
      {
        val checkbox: Elem = ajaxCheckbox(series.season(season_number).forall(_.watched), watched => {
          series.mark_season_watched(season_number, watched);
          JsCmds.RedirectTo("")
        })
        ".seasonLink *" #> season_link(series, season_number) &
        ".watched *" #> (checkbox % ("id" -> (series_id + "_Season_" + season_number)))
      }
      )

      ".seriesName" #> series.name &
      "#seasons *" #> bindEpisodes
  }

  def season_link(series: Series, season_number: Int): NodeSeq = {
    val text: String = series.name + " Season " + season_number
    season_link(series, season_number, text)
  }
}

