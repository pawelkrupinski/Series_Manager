package net.pawel {

import net.pawel.model.{Episode, Series}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import xml._
import net.liftweb.http.{CometActor, S}
import net.pawel.snippet.{Season_Link, Series_Link}

package comet {

class List_Episodes extends CometActor with Series_Link with Season_Link {

  def render = {
    val params: Array[String] = name.open_!.split(':')
    val series: Series = Series.find_by_id(params(0).toLong).open_!
    val season_number: Int = params(1).toInt
    val episodes = series.season(season_number).sortBy(_.number.toString.toInt)

    ".seriesName" #> series_link(series) &
    ".season" #> season_number &
    ".episodes *" #> bindEpisodesCss(episodes)
  }

  def bindEpisodesCss(episodes: List[Episode]) = episodes.map(episode =>
    ".season *" #> episode.season
    & ".episode *" #> episode.number
    & ".aired *" #> episode.aired
    & ".id *" #> episode.episode_id
    & ".name *" #> attachOverview(a(Text(episode.name), JsCmds.Noop), episode.overview)
    & ".season *" #> episode.season
    & ".series_name *" #> season_link(episode.series, episode.season, episode.series.name)
    & ".overview *" #> episode.overview
    & ".watched *" #> ajaxCheckbox(episode.watched, watched => this ! Mark_Episode_Watched(episode)))

  def attachOverview(elem: Elem, overview: String) =
    (elem % ("onmouseover" -> ("tooltip.show('" + overview.replace("'", "\\'") + "')"))
         % ("onmouseout" -> "tooltip.hide()"))

  case class Mark_Episode_Watched(episode: Episode)

  override def lowPriority = {
    case Mark_Episode_Watched(episode) => episode.mark_watched(); reRender()
  }
}
}

package snippet {

class List_Episodes_Snippet {
  def liftComet: String = {
    "lift:comet?type=List_Episodes?name=" + S.param("series_id").open_! + ":" + S.param("season").open_! + "?eager_eval=true"
  }

  def render(in: NodeSeq): NodeSeq = {
      new scala.xml.transform.RewriteRule {
        override def transform(n: Node): Seq[Node] = n match {
          case elem: Elem if (elem.attribute("class").filter(_.contains(Text("replace_comet"))).isDefined) => {
            elem % ("class" -> liftComet)
          }
          case elem: Elem => elem copy (child = elem.child flatMap (this transform))
          case other => other
        }
      }.transform(in.head)
    }
  }
}
}