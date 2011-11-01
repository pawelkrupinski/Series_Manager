package net.pawel.snippet.main_page

import java.util.Date
import org.joda.time.DateTime
import net.pawel.model.{Series, Episode}

trait Episode_Fetching {
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

  def episodes = Series.findAll().flatMap(series => series.episodes.filterNot(_.watched))
    .groupBy(_.series_id.toLong).values.map(_.sorted.take(2)).flatten.toList
}