package net.pawel.comet

import net.pawel.snippet.Series_Link
import net.pawel.model.Series
import net.liftweb.common.Logger
import net.liftweb.http.CometActor
import net.pawel.lib.{Remove_Listener, Add_Listener, Episode_Manager, Updated_Watched}

class List_Episodes extends CometActor with Series_Link with Episode_Binding_Comet with Logger {

  var info: List_Episodes_Info = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    info = new List_Episodes_Info(params(0).toLong, Series.find_by_id(params(1).toLong).open_!, params(2).toInt)
    Episode_Manager ! Add_Listener(this, info.user_id)
  }

  override protected def localShutdown() {
    super.localShutdown()
    Episode_Manager ! Remove_Listener(this, info.user_id)
  }

  def render = {
    val episodes = info.series.season(info.season_number).sortBy(_.number.toString.toInt)

    ".seriesName" #> series_link(info.series) &
    ".season" #> info.season_number &
    ".episodes *" #> bindEpisodesCss(episodes, info.user_id)
  }

  override def lowPriority = {
    case message: Updated_Watched => if (info.overlaps(message)) reRender();
  }
}

class List_Episodes_Info(val user_id: Long, val series: Series, val season_number: Int) {
  def overlaps(message: Updated_Watched) = {
    val fromIsBefore: Boolean = message.from.map(_.season <= season_number).getOrElse(true)
    val toIsAfter: Boolean = message.to.map(_.season >= season_number).getOrElse(false)
    val fromIsAfter: Boolean = message.from.map(_.season >= season_number).getOrElse(false)
    val toIsBefore: Boolean = message.to.map(_.season <= season_number).getOrElse(true)
    same_series(message.series_id) &&
      ((fromIsBefore && toIsAfter) || (fromIsAfter && toIsBefore))
  }

  def same_series(series_id: Long) = series_id == series.series_id.get
}