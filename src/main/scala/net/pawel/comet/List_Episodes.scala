package net.pawel.comet

import net.pawel.snippet.Series_Link
import net.pawel.model.Series
import net.liftweb.common.Logger
import net.liftweb.http.CometActor
import net.pawel.lib.{Remove_Listener, Add_Listener, Episode_Manager, Updated_Watched}

class List_Episodes extends CometActor with Series_Link with Episode_Binding_Comet with Logger {

  var userId: Long = _
  var series: Series = _
  var season_number: Int = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    userId = params(0).toLong
    series = Series.find_by_id(params(1).toLong).open_!
    season_number = params(2).toInt
    Episode_Manager ! Add_Listener(this, userId)
  }

  override protected def localShutdown() {
    Episode_Manager ! Remove_Listener(this, userId)
  }

  def render = {
    val episodes = series.season(season_number).sortBy(_.number.toString.toInt)

    ".seriesName" #> series_link(series) &
    ".season" #> season_number &
    ".episodes *" #> bindEpisodesCss(episodes, userId)
  }

  override def lowPriority = {
    case Updated_Watched(from, to) => reRender();
  }
}