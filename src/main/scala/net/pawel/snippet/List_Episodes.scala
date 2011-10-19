package net.pawel.snippet

import main_page.Episode_Binding
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.pawel.model.Series
import xml._
import net.pawel.injection.Injected
class List_Episodes extends Injected with Episode_Binding with Series_Link{

  def render = {
    val series: Series = Series.find_by_id(S.param("series_id").open_!.toLong).open_!
    val season_number: Int = S.param("season").open_!.toInt
    val episodes = series.season(season_number).sortBy(_.number.toString.toInt)

    ".seriesName" #> series_link(series) &
    ".season" #> season_number &
    ".episodes *" #> bindEpisodesCss(episodes)
  }
}
