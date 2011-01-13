package net.pawel.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.pawel.model.Series
import net.liftweb.http.SHtml._
import xml.{NodeSeq, Text}

trait Series_Link {
  def series_link(series: Series) = link("/series/seasons?series_id=" + series.series_id, () => {}, Text(series.name)) % ("id" -> series.series_id)
}