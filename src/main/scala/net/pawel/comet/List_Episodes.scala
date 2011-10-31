package net.pawel {

import net.pawel.model.{Episode, Series}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import xml._
import net.liftweb.http.{CometActor, S}
import net.pawel.snippet.{Season_Link, Series_Link}

package comet {

import net.liftweb.actor.LiftActor

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


}


}