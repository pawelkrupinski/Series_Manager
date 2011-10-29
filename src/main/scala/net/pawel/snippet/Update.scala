package net.pawel.snippet

import xml.NodeSeq
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import net.pawel.services.Series_Service._
import net.pawel.model.{Episode, Series}
import collection.immutable.Map
import net.liftweb.common.Logger

class Update extends Logger {

  def render(in: NodeSeq) = ajaxButton("Update", () => { update; JsCmds.Noop})

  def update {
    val series: List[Series] = Series.findAll
    series.par.flatMap(updateSeries(_))
  }

  def updateSeries(series: Series) = {
    debug("Updating " + series.name)
    val existing_episodes = series.episodes.map(_.key).zip(series.episodes).toMap
    debug("Fetching online episodes for " + series.name)
    val online_episodes = fetch_episodes(series)

    def updateEpisode(online_episode: Episode): Option[Episode] =
      existing_episodes.get(online_episode.key) match {
        case Some(existing_episode) => {
          debug("Existing episode found for series " + existing_episode.series.name + " season " + existing_episode.season +
          " episode number " + existing_episode.number)
          existing_episode.update(online_episode)
        }
        case None => {
          debug("Existing episode not found for series " + online_episode.series.name + " season " + online_episode.season +
          " episode number " + online_episode.number + ". Adding it.")
          online_episode.series.last_watched.foreach(episode => if (episode > online_episode) online_episode.watched(true))
        }

          online_episode.save;

          Some(online_episode)
        }

    online_episodes.par.flatMap(updateEpisode)
  }
}