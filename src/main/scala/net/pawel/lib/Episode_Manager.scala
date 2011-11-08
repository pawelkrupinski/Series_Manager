package net.pawel.lib

import net.pawel.model.Episode
import net.liftweb.actor.LiftActor
import net.liftweb.common.Logger

object Episode_Manager extends LiftActor with Logger {
  var listeners = Map[Long, Set[LiftActor]]()

  override protected def messageHandler = {
    case Mark_Episode_Watched(episode, userId) => {
      val from: Option[Episode] = episode.series.last_watched_episode
      val to: Option[Episode] = episode.mark_watched()
      debug("Updating watched episode from " + from + " to " + to)
      val updated: Updated_Watched = Updated_Watched.from(from).to(to)
      updateListeners(userId, updated)
    }

    case Add_Listener(actor, userId) => {
      val updatedSet: Set[LiftActor] = listenersFor(userId) + actor
      listeners = listeners.updated(userId, updatedSet)
    }

    case Remove_Listener(actor, userId) => {
      val updatedSet: Set[LiftActor] = listenersFor(userId) - actor
      if (updatedSet.isEmpty) {
        listeners -= userId
      } else {
        listeners = listeners.updated(userId, updatedSet)
      }
    }
  }

  def listenersFor(userId: Long): Set[LiftActor] = {
    listeners.get(userId).getOrElse(Set.empty)
  }

  def updateListeners(userId: Long, message: Any) {
    listenersFor(userId).foreach(_ ! message)
  }
}

case class Mark_Episode_Watched(episode: Episode, userId: Long)

case class Add_Listener(actor: LiftActor, userId: Long)

case class Remove_Listener(actor: LiftActor, userId: Long)

case class Updated_Watched(from: Option[Episode], to: Option[Episode]) {
  def series_id: Long = from.map(_.series_id).orElse(to.map(_.series_id)).get
}

object Updated_Watched {
  def from(fromEpisode: Option[Episode]) = new {
    def to(toEpisode: Option[Episode]) = new Updated_Watched(fromEpisode, toEpisode)
  }
}