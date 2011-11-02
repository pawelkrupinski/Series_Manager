package net.pawel.model

import net.liftweb.mapper._
import xml.NodeSeq
import Implicits._
import net.liftweb.common.{Logger, Empty, Box}

class Series extends LongKeyedMapper[Series] with IdPK {
  def getSingleton = Series
  object name extends MappedPoliteString(this, 128)
  object series_id extends MappedLong(this)
  object active extends MappedBoolean(this)
  object last_watched_episode_id extends MappedLongForeignKey(this, Episode)

  def episodes: List[Episode] = Episode.find_by_series_id(series_id)
  def season(season: Int): List[Episode] = Episode.find_by_series_id_and_season(series_id, season)
  def last_watched_episode: Option[Episode] = Episode.findByKey(last_watched_episode_id)
  def unmark_last_watched = { last_watched_episode_id(Empty); save(); }
  def mark_last_watched(episode: Episode) = { last_watched_episode_id(episode.id); save(); episode; }

  def delete {
    episodes.foreach(_.delete_!)
    delete_!
  }

  def mark_season_watched(season_number: Int, watched: Boolean) = Some(season(season_number).sorted)
    .map(season => if (watched) season.last else season.head).map(_.mark_watched(watched))
}

object Series extends Series with LongKeyedMetaMapper[Series] {
  def find_by_id(id: Long): Box[Series] = Series.find(By(Series.series_id, id), By(Series.active, true))
  def id_exists(id: Long) = find_by_id(id).isDefined

  def save_series(id: Long, name: String) {
    val series: Series = new Series
    series.series_id(id)
    series.name(name)
    series.save
  }

  def from(xml: NodeSeq) =  new Series().series_id(xml.long("seriesid")).name(xml("SeriesName"))
}

class Episode extends LongKeyedMapper[Episode] with IdPK with Ordered[Episode] with Logger {
  def getSingleton = Episode

  object series_id extends MappedLongForeignKey(this, Series)
  object episode_id extends MappedLong(this)

  object season extends MappedInt(this)
  object number extends MappedInt(this)
  object name extends MappedPoliteString(this, 128)
  object aired extends MappedDate(this)
  object overview extends MappedText(this)
  object last_updated extends MappedLong(this)
  lazy val series: Series = Series.find_by_id(series_id).open_!
  def watched = series.last_watched_episode.map(_ >= this).getOrElse(false)

  def compare(that: Episode) = order - that.order

  def order = (season.get * 10000) + number

  def key:(Long, Int, Int) = (series_id.get, season.get, number.get)

  def mark_watched(): Option[Episode] = mark_watched(!watched)

  def isLastWatched = series.last_watched_episode.map(last_watched => last_watched.id == this.id).getOrElse(false)

  def mark_watched(watched_state: Boolean): Option[Episode] = {
    debug("Marking " + this + " watched: " + watched_state)
    series.unmark_last_watched
    if (watched_state == true) {
      Some(series.mark_last_watched(this))
    } else {
      series.episodes.sorted.reverse.find(_ < this).map(episode => series.mark_last_watched(episode))
    }
  }

  def update(other: Episode): Option[Episode] =
    if (other.last_updated > last_updated) {
      delete_!
      other.save
      Some(other)
    } else None

  def find_all_for_the_series:List[Episode] = Episode.find_by_series_id(series_id)
}

object Episode extends Episode with LongKeyedMetaMapper[Episode] with CRUDify[Long, Episode] {
  def find_by_series_id(id: Long): List[Episode] = findAll(By(Episode.series_id, id))
  def find_by_series_id_and_season(id: Long, season: Int) = findAll(By(Episode.series_id, id), By(Episode.season, season))

  def from(xml: NodeSeq) = {
    val episode: Episode = new Episode
    episode.series_id(xml.long("seriesid"))
    episode.episode_id(xml.long("id"))
    episode.season(xml.int("SeasonNumber"))
    episode.number(xml.int("EpisodeNumber"))
    episode.name(xml("EpisodeName"))
    episode.aired(xml.date("FirstAired"))
    episode.overview(xml("Overview"))
    episode.last_updated(xml.long("lastupdated"))
    episode
  }
}