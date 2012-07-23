package net.pawel.snippet

import net.liftweb.util.Helpers._
import net.pawel.model.Series
import net.liftweb.util.ClearClearable

class Series_List {
  def bindSeries(series: Series) =
    "a [href]" #> ("/series/series?series_id=" + series.series_id) &
    "a *" #> series.name

  def render = "li" #> Series.findAll().sortBy(_.name.get).map(series => bindSeries(series)) & ClearClearable
}
