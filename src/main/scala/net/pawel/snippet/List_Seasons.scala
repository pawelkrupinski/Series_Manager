package net.pawel.snippet

import xml.{NodeSeq, Text}
import net.pawel.model.{Series, Episode}
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import java.lang.String

class List_Seasons {

  def render(in: NodeSeq) = {
    import List_Seasons._
    val series_id: Long = S.param("series_id").open_!.toLong
    val series: Series = Series.find_by_id(series_id).open_!
    val seasons: List[Int] = Episode.find_by_series_id(series_id).map(_.season.toString.toInt).toSet.toList.sorted

    def bindEpisodes(template: NodeSeq): NodeSeq = seasons.flatMap(season_number =>
      bind("season", template,
      "seasonLink" -> season_link(series, season_number),
      "watched" -> ajaxCheckbox (series.season(season_number).forall(_.watched),
        watched => {series.mark_season_watched(season_number, watched); JsCmds.RedirectTo("")})
        % ("id" -> (series_id + "_Season_" + season_number))
      ))

    bind("seasons", in,
      "seriesName" -> series.name,
      "list" -> bindEpisodes _)
  }
}

object List_Seasons {

  def season_link(series: Series, season_number: Int, text: String): NodeSeq = {
    link("/episode/list?series_id=" + series.series_id + "&season=" + season_number, () => {}, Text(text))
  }

  def season_link(series: Series, season_number: Int): NodeSeq = {
    val text: String = series.name + " Season " + season_number
    season_link(series, season_number, text)
  }
}