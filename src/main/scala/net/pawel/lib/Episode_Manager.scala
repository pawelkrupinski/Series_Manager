package net.pawel.lib

import net.liftweb.http.{ListenerManager, SessionVar}
import net.pawel.model.Episode
import net.liftweb.actor.LiftActor
import net.liftweb.common.Logger

class Episode_Manager extends ListenerManager with LiftActor with Logger {
  override protected def lowPriority ={
    case Mark_Episode_Watched(episode) => {
      val from: Option[Episode] = episode.series.last_watched_episode
      val to: Option[Episode] = episode.mark_watched()
      debug("Updating watched episode from " + from + " to " + to)
      updateListeners(Updated_Watched.from(from).to(to))
    };
  }

  protected def createUpdate = None
}

object Episode_Manager extends SessionVar(new Episode_Manager())

case class Mark_Episode_Watched(episode: Episode)

case class Updated_Watched(from: Option[Episode], to: Option[Episode])
object Updated_Watched {
  def from(fromEpisode: Option[Episode]) = new {
    def to(toEpisode: Option[Episode]) = new Updated_Watched(fromEpisode, toEpisode)
  }
}