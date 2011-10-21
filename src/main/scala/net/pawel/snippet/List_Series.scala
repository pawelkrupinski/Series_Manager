package net.pawel.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.liftweb.http.SHtml._
import xml.{NodeSeq, Text}
import net.pawel.model.Series

class List_Series extends Series_Link {
  def render = ".list *" #> Series.findAll.sortBy(_.name.toString).map(series =>
      ".episodesLink" #> series_link(series) &
        ".delete" #> submit("Delete", () => series.delete)
    )
}