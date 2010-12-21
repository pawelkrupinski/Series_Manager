package net.pawel.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.RedirectTo
import net.pawel.model.{Episode, Series}
import net.liftweb.common.Box
import io.Source
import xml.{XML, Text, NodeSeq}
import java.io.StringReader
import java.net.URLEncoder
import net.pawel.services.{ZipWrapper, Http}
import net.pawel.services.Series_Service._

class SearchResults {

  def render(in: NodeSeq) = {
    val nameParameter: Box[String] = S.param("name")
    val query = nameParameter match {
      case box: Box[String] => box.open_!
    }
    bind("series", in,
      "query" -> query,
      "results" -> result(query)
    )
  }

  def result(query: String): (NodeSeq => NodeSeq) = xhtml => bind("series", xhtml, "result" -> seriesRows(query))

  def seriesRows(query: String) = {
    val series = find_series(query)
    series.map(seriesRow(_))
  }

  def seriesRow(series: Series) = if (Series.id_exists(series.series_id)) label(series.name) else addSeriesLink(series)

  def label(name: String) = <span>{name}<br /></span>
  def addSeriesLink(series: Series) = <span>{
    a(() => {create_series(series); RedirectTo("/series/list")}, Text(series.name)) % ("id" -> series.series_id.toString)
    }<br /></span>
}