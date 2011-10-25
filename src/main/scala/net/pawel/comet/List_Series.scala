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

  def render = ".list *" #> Series.findAll.sortBy(_.name.toString).map(series =>
        ".episodesLink" #> series_link(series) &
        ".delete" #> deleteOption(series)
      )

  def deleteOption(series: Series) =
    if (series.active)
      ajaxButton("Delete", () => {this ! Delete(series); JsCmds._Noop})
    else
      Text("")

  def async_create_series(series: Series) = Creation_Overseer ! Create(series, this)

  def loading = <img/> % ("src" -> "/images/ajax-loader.gif") %("width" -> "15px") % ("height" -> "15px")

  def series_link(series: Series) = if (series.active) {link("/series/seasons?series_id=" + series.series_id, () => {}, Text(series.name)) %
      ("id" -> series.series_id)} else { async_create_series(series); List(loading, Text(" " + series.name)) }

  override protected def dontCacheRendering = true

  override def lowPriority = {
    case Delete(series) => series.delete; reRender()
    case Refresh => reRender()
  }

  case class Delete(series: Series)
}

case object Refresh
case class Create(series: Series, actor: List_Series)

object Creation_Overseer extends LiftActor {
  var listeners = Map[Long, List[LiftActor]]()
  protected def messageHandler = {
    case Create(series, actor) => if (listeners.contains(series.series_id)) {
      val list: List[LiftActor] = listeners(series.series_id)
      listeners += (series.series_id.get -> (actor :: list))
    } else {
      listeners += (series.series_id.get -> List(actor))
      new Series_Creator(series)
    }
    case Created(seriesId) => listeners.get(seriesId).flatten.foreach(_ ! Refresh); listeners -= seriesId
  }

  case class Created(seriesId: Long)

  class Series_Creator(val series: Series) extends LiftActor {
    case class Create(series: Series)

    this ! Create(series)

    protected def messageHandler = {
      case Create(series) => {
        create_series(series)
        Creation_Overseer ! Created(series.series_id)
      }
    }
  }
}


