package net.pawel.snippet

import net.pawel.model.{Series, Episode}
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import java.lang.String
import xml.{Elem, NodeSeq, Text}

trait Season_Link {
  def season_link(series: Series, season_number: Int, text: String): NodeSeq = {
    link("/episode/list?series_id=" + series.series_id + "&season=" + season_number, () => {}, Text(text))
  }
}