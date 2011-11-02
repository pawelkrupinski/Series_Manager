package net.pawel.snippet

import xml.NodeSeq
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import net.pawel.services.Series_Service._
import net.pawel.model.{Episode, Series}
import net.liftweb.common.Logger
import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import collection.immutable.Map

class Update {
  def render(in: NodeSeq) = ajaxButton("Update", () => { Update.update; JsCmds.Noop})
}

object Update extends LiftActor with Logger with ListenerManager {

  protected def createUpdate = None

  def update {
    this ! Update
  }

  case object Update

  override protected def messageHandler = {
    case Update => Series.findAll.par.foreach(updateSeries(_))
  }

  def toKey(episode: Episode): (Long, Int, Int) = (episode.series_id, episode.season, episode.number)

  def updateSeries(series: Series) = {
    debug("Updating " + series.name)
    val existing_episodes = series.episodes.map(_.key).zip(series.episodes).toMap
    debug("Fetching online episodes for " + series.name)
    val online_episodes = fetch_episodes(series)

    def updateEpisode(online_episode: Episode): Option[Episode] = {
      existing_episodes.get(online_episode.key) match {
        case Some(existing_episode) => {
          debug("Existing episode found for series " + existing_episode.series.name + " season " +
            existing_episode.season + " episode number " + existing_episode.number)
          existing_episode.update(online_episode)
          updateListeners(Episode_Updated(existing_episode))
        }
        case None => {
          debug("Existing episode not found for series " + online_episode.series.name + " season " +
            online_episode.season + " episode number " + online_episode.number + ". Adding it.")
          updateListeners(Episode_Added(online_episode))
        }
      }
      online_episode.save;
      Some(online_episode)
    }
    online_episodes.par.foreach(updateEpisode)
    val bySeriesSeasonAndNumber: Map[(Long, Int, Int), List[Episode]] = Episode.findAll().groupBy(toKey)
    bySeriesSeasonAndNumber.values.filter(_.size > 1).par.foreach(removeDuplicates)
  }

  def removeDuplicates(episodes: List[Episode]) {
    val remaining: Episode = episodes.find(_.isLastWatched).getOrElse(episodes.head)
    episodes.filterNot(_.id == remaining.id).foreach(_.delete_!)
  }
}

case class Episode_Updated(episode: Episode)
case class Episode_Added(episode: Episode)

