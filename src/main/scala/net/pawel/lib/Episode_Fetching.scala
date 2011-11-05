package net.pawel.lib

import java.util.Date
import org.joda.time.DateTime
import net.pawel.model.Episode
import net.liftweb.http.CometListener
import net.liftweb.common.Logger

trait Episode_Fetching extends CometListener with Logger {
  var episodes: List[Episode] = Nil

  implicit def dateToDateTime(date: Date) = new DateTime(date)

  def now = new DateTime
  def month_ago = now.minusMonths(1)

  val unaired: (Episode) => Boolean = episode => Option(episode.aired.is).map(_.isAfter(now)).getOrElse(true)
  val unwatched: (Episode) => Boolean = !unaired(_)
  val aired_more_than_month_ago: (Episode) => Boolean = _.aired.get.isBefore(month_ago)
  val aired_less_than_month_ago: (Episode) => Boolean = !aired_more_than_month_ago(_)

  def recently_aired_episodes: List[Episode] = episodes.filter(unwatched).filter(aired_less_than_month_ago)
  def unwatched_episodes: List[Episode] = episodes.filter(unwatched).filter(aired_more_than_month_ago)
  def unaired_episodes: List[Episode] = episodes.filter(unaired).sortWith((f, s) => {
    val firstDate: Option[Date] = Option(f.aired.is)
    val secondDate: Option[Date] = Option(s.aired.is)
    if (secondDate.isEmpty) true
    else if (firstDate.isEmpty) false
    else firstDate.get.isBefore(secondDate.get)
  })

  protected def registerWith = {
    debug("Registering with")
    Episode_Provider.is
  }

  override def lowPriority = {
    case list: List[Episode] => episodes = list; reRender();
  }
}