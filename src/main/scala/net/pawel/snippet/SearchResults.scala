package net.pawel.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds.RedirectTo
import net.pawel.model.Series
import net.liftweb.common.Box
import net.pawel.services.Series_Service._
import net.liftweb.http.S
import xml.{NodeSeq, Text}

class SearchResults {

  def render = {
    val nameParameter: Box[String] = S.param("name")
    val query = nameParameter.getOrElse("")
    ".query" #> query &
    ".results *" #> result(query)
  }

  def result(query: String) = ".result *" #> seriesRows(query)

  def seriesRows(query: String): List[NodeSeq] = find_series(query).sortBy(_.name.get).map(toSeriesRow)

  def toSeriesRow(series: Series) = if (Series.id_exists(series.series_id)) Text(series.name) else addSeriesLink(series)

  def addSeriesLink(series: Series) = a(() => {create_inactive(series); RedirectTo("/series/list")},
    Text(series.name)) % ("id" -> series.series_id.toString)

  def listSeries = S.session.map(session => session.findComet("List_Series")).openOr(Nil)
}