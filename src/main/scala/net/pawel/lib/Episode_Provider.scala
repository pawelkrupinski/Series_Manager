package net.pawel.lib

import net.liftweb.actor.LiftActor
import net.liftweb.common.Logger
import net.pawel.model.{User, Series, Episode}
import net.liftweb.http._
import net.pawel.snippet.{Series_Updated, Update}

class Episode_Provider extends LiftActor with ListenerManager with Logger {
  val userId: Long = User.currentUser.open_!.id
  Episode_Manager ! Add_Listener(this, userId)
  Update ! AddAListener(this, {case _ => true })

  S.session.foreach(_.addSessionCleanup(a => {
    Episode_Manager ! Remove_Listener(Episode_Provider.this, userId)
    Update ! RemoveAListener(this)
  }))

  var episodes = episodes_fetch

  private def episodes_fetch = Series.findAll().flatMap(series => {
    val last_watched_episode: Option[Episode] = series.last_watched_episode
    series.episodes.filterNot(_.watched(last_watched_episode))
  }).groupBy(_.series_id.toLong).values.map(_.sorted.take(2)).flatten.toList

  def refresh_episodes() {
    episodes = episodes_fetch
  }

  override protected def lowPriority = {
    case Updated_Watched(from, to) => refresh_episodes(); updateListeners();
    case Series_Updated(series) => refresh_episodes(); updateListeners();
  }

  protected def createUpdate = episodes
}

object Episode_Provider extends SessionVar[Episode_Provider](new Episode_Provider)



