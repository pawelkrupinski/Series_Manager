package net.pawel.snippet.main_page

import net.pawel.model.Episode
import java.util.Date
import org.joda.time.DateTime

trait Episode_Fetching {
  implicit def dateToDateTime(date: Date) = new DateTime(date)

  def now = new DateTime
  def month_ago = now.minusMonths(1)

  val unaired: (Episode) => Boolean = episode => Option(episode.aired.get).map(_.isAfter(now)).getOrElse(false)
  val unwatched: (Episode) => Boolean = !unaired(_)
  val aired_more_than_month_ago: (Episode) => Boolean = _.aired.get.isBefore(month_ago)
  val aired_less_than_month_ago: (Episode) => Boolean = !aired_more_than_month_ago(_)

  def recently_aired_episodes: List[Episode] = episodes.filter(unwatched).filter(aired_less_than_month_ago)

  def episodes: List[Episode] = Episode.find_unwatched.groupBy(_.series_id.toLong).iterator
    .map((t: (Long, List[Episode])) => t._2.sortBy(e => e.season * 1000 + e.number).take(2)).toList.flatten
}