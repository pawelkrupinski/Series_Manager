package net.pawel.comet

import net.liftweb.http.SHtml._
import net.pawel.model.Series
import net.liftweb.http.CometActor
import net.pawel.snippet.Series_Link
import net.liftweb.http.js.JsCmds

class List_Series extends Series_Link with CometActor {
  def render = ".list *" #> 
    Series.findAll.sortBy(_.name.toString).map(series =>
      ".episodesLink" #> series_link(series) &
      ".delete" #> ajaxButton("Delete", () => { this ! Delete(series); JsCmds._Noop })
    )


  override protected def dontCacheRendering = true

  override def lowPriority = {
    case Delete(series) => { series.delete; reRender(); }
  }

  case class Delete(series: Series)
}

