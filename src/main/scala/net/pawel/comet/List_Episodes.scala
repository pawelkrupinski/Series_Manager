package net.pawel.comet

import net.pawel.model.Series
import net.liftweb.http.CometActor
import net.pawel.snippet.Series_Link


class List_Episodes extends CometActor with Series_Link with Episode_Binding_Comet {

  def render = {
    val params: Array[String] = name.open_!.split(':')
    val series: Series = Series.find_by_id(params(0).toLong).open_!
    val season_number: Int = params(1).toInt
    val episodes = series.season(season_number).sortBy(_.number.toString.toInt)

    ".seriesName" #> series_link(series) &
    ".season" #> season_number &
    ".episodes *" #> bindEpisodesCss(episodes)
  }
}