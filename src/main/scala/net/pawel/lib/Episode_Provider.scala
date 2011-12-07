package net.pawel.lib

import net.liftweb.common.Logger
import net.pawel.model.{Series, Episode}
import net.liftweb.http._
import net.pawel.snippet.{Series_Updated, Update}

trait Episode_Provider extends CometActor with Registers_User with Logger {
  def register(userId: Long) {
    super.localSetup();
    debug("Registering for user" + userId)
    Episode_Manager ! Add_Listener(this, userId)
    Update ! AddAListener(this, {case _ => true })
  }

  def unregister(userId: Long) {
    super.localShutdown();
    Episode_Manager ! Remove_Listener(Episode_Provider.this, userId)
    Update ! RemoveAListener(this)
  }

  var episodes = episodes_fetch

  private def episodes_fetch = Series.findAll().flatMap(series => {
    val last_watched_episode: Option[Episode] = series.last_watched_episode
    series.episodes.filterNot(_.watched(last_watched_episode))
  }).groupBy(_.series_id.toLong).values.map(_.sorted.take(2)).flatten.toList

  def refresh_episodes() {
    episodes = episodes_fetch
  }

  override def lowPriority = {
    case Updated_Watched(from, to) => refresh_episodes(); reRender();
    case Series_Updated(series) => refresh_episodes(); reRender();
  }
}


