package net.pawel.snippet

import xml.NodeSeq
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds
import net.pawel.services.Series_Service._
import net.pawel.model.{Episode, Series}
class Update {

  def render(in: NodeSeq) = ajaxButton("Update", () => { update; JsCmds.Noop})

  def update {
    val series: List[Series] = Series.findAll
    series.flatMap(updateSeries(_))
  }

  def updateSeries(series: Series): List[Episode] = {
    println("Updating " + series.name)
    val existing_episodes = series.episodes.map(e => (e.episode_id, e)).toMap
    val online_episodes = fetch_episodes(series)

    def updateEpisode(online_episode: Episode): Option[Episode] =
      existing_episodes.get(online_episode.episode_id) match {
        case Some(existing_episode) => existing_episode.update(online_episode)
        case None => {online_episode.save; Some(online_episode)}
      }

    online_episodes.flatMap(updateEpisode(_) match {
      case Some(episode) => List(episode)
      case None => Nil
    })
  }
}