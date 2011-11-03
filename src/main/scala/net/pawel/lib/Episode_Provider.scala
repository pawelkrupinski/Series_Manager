package net.pawel.lib

import net.pawel.model.{Series, Episode}
import net.liftweb.actor.LiftActor
import net.liftweb.http.{ListenerManager, AddAListener, SessionVar}
import net.liftweb.common.Logger

class Episode_Provider extends LiftActor with ListenerManager with Logger {
  Episode_Manager ! AddAListener(this, { case _ => true })

  var episodes = episodes_fetch

  private def episodes_fetch = Series.findAll().flatMap(series => {
    val last_watched_episode: Option[Episode] = series.last_watched_episode
    series.episodes.filterNot(_.watched(last_watched_episode))
  }).groupBy(_.series_id.toLong).values.map(_.sorted.take(2)).flatten.toList


  override protected def lowPriority = {
    case Updated_Watched(from, to) => episodes = episodes_fetch; updateListeners();
  }

  protected def createUpdate = episodes
}

object Episode_Provider extends SessionVar[Episode_Provider](new Episode_Provider)



