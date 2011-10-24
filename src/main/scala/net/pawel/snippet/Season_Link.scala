package net.pawel.snippet

import net.pawel.model.Series
import net.liftweb.http.SHtml._
import java.lang.String
import xml.{NodeSeq, Text}

trait Season_Link {
  def season_link(series: Series, season_number: Int, text: String): NodeSeq = {
    link("/episode/list?series_id=" + series.series_id + "&season=" + season_number, () => {}, Text(text))
  }
}