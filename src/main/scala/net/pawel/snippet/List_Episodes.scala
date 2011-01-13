package net.pawel.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.S
import net.pawel.model.{Episode, Series}
import net.liftweb.http.js.JsCmds
import net.liftweb.common._
import xml._
import java.util.Date
import org.joda.time.DateTime
import net.pawel.services.Series_Service
import net.pawel.injection.Injected
import com.google.inject.Inject

class List_Episodes extends Injected with Episode_Binding with Series_Link{

  def render(in: NodeSeq) = {
    val series: Series = Series.find_by_id(S.param("series_id").open_!.toLong).open_!
    val season_number: Int = S.param("season").open_!.toInt
    val episodes = series.season(season_number).sortBy(_.number.toString.toInt)

    bind("episodes", in,
      "seriesName" -> series_link(series),
      "season" -> season_number,
      "list" -> bindEpisodes(episodes) _)
  }
}
