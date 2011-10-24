package net.pawel.comet

import net.liftweb.http.SHtml._
import net.pawel.model.Series
import net.liftweb.http.js.JsCmds
import xml._
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.pawel.services.Series_Service._
import net.liftweb.actor.LiftActor

class List_Series extends CometActor {

  def render = {
    ".list *" #>
      Series.findAll.sortBy(_.name.toString).map(series =>
        ".episodesLink" #> series_link(series) &
          ".delete" #> ajaxButton("Delete", () => { this ! Delete(series); JsCmds._Noop })
      )
  }

  def async_create_series(series: Series) = (new Series_Creator).async_create_series(series, this)

  def loading = <img/> % ("src" -> "/images/ajax-loader.gif") %("width" -> "15px") % ("height" -> "15px")

  def series_link(series: Series) = if (series.active) {link("/series/seasons?series_id=" + series.series_id, () => {}, Text(series.name)) %
      ("id" -> series.series_id)} else { async_create_series(series); List(loading, Text(" " + series.name)) }

  def createAndLoad(series: Series) = {
    val elem: Elem = <span class="lift:LazyLoad">
      {<span/> %
        ("class" -> ("lift:Create_Series?id=" + series.series_id.toString))}
    </span>
    println(elem.mkString)
    elem
  }

  override protected def dontCacheRendering = true

  override def lowPriority = {
    case Delete(series) => series.delete; reRender()
    case Refresh => reRender()
  }

  case class Delete(series: Series)
}

case object Refresh

class Series_Creator extends LiftActor {
  case class Create(series: Series, actor: List_Series)
  def async_create_series(series: Series, actor: List_Series) = { this ! Create(series, actor)}

  protected def messageHandler = {
    case Create(series, actor) => create_series(series); actor ! Refresh
  }
}
