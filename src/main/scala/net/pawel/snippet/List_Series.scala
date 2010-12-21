package net.pawel.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.liftweb.http.SHtml._
import xml.{NodeSeq, Text}
import net.pawel.model.Series

class List_Series {
  def render(in: NodeSeq) = bind("series", in,
      "list" -> ((template: NodeSeq) => Series.findAll.sortBy(_.name.toString)
        .flatMap(series => bind("series", template,
        "episodesLink" -> link("/series/seasons?series_id=" + series.series_id, () => {}, Text(series.name)) % ("id" -> series.series_id),
        "delete" -> submit("Delete", () => series.delete)
      ))))
}