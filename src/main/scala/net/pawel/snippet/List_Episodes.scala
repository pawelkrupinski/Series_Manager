package net.pawel.snippet

import net.pawel.snippet.main_page.Episode_Binding
import net.pawel.snippet.Series_Link
import net.pawel.model.{Episode, Series}
import net.liftweb.http.S
import net.pawel.injection.Injected
import net.liftweb.util.Helpers._
class List_Episodes extends Injected with Episode_Binding with Series_Link {

  def render = {
    val series: Series = Series.find_by_id(S.param("series_id").open_!.toLong).open_!
    val season_number: Int = S.param("season").open_!.toInt
    val episodes = series.season(season_number).sortBy(_.number.toString.toInt)

    ".seriesName" #> series_link(series) &
    ".season" #> season_number &
    ".episodes *" #> bindEpisodesCss(episodes)
  }

//  override def lowPriority = {
//    case Mark_Episode_Watched(episode) => episode.mark_watched(); reRender()
//  }
}

case class Mark_Episode_Watched(episode: Episode)