package net.pawel.model

import net.liftweb.mapper._
import xml.NodeSeq
import java.text.{ParseException, SimpleDateFormat}
import Implicits._
import net.liftweb.common.Box

class Series extends LongKeyedMapper[Series] with IdPK {
  def getSingleton = Series
  object name extends MappedPoliteString(this, 128)
  object series_id extends MappedLong(this)
  object active extends MappedBoolean(this)
  def episodes: List[Episode] = Episode.find_by_series_id(series_id)
  def season(season: Int): List[Episode] = Episode.find_by_series_id_and_season(series_id, season)

  def delete {
    episodes.foreach(_.delete_!)
    delete_!
  }

  def mark_season_watched(season_number: Int, watched: Boolean) = Some(season(season_number).sorted)
    .map(season => if (watched) season.last else season.head).map(_.mark_watched(watched))
}

object Series extends Series with LongKeyedMetaMapper[Series] with CRUDify[Long, Series] {
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

class Episode extends LongKeyedMapper[Episode] with IdPK {
  def getSingleton = Episode

  object series_id extends MappedLongForeignKey(this, Series)
  object episode_id extends MappedLong(this)

  object season extends MappedInt(this)
  object number extends MappedInt(this)
  object name extends MappedPoliteString(this, 128)
  object aired extends MappedDate(this)
  object overview extends MappedText(this)
  object last_updated extends MappedLong(this)
  object watched extends MappedBoolean(this)
  lazy val series: Series = Series.find_by_id(series_id).open_!

  def update_watched(watched_state: Boolean): Unit = {
    watched(watched_state)
    save
  }

  def mark_watched(watched_state: Boolean) {
    update_watched(watched_state)
    if (watched_state == true) {
      mark_previous
    } else {
      mark_next
    }
  }

  def update(other: Episode): Option[Episode] =
    if (other.last_updated > last_updated) {
      other.watched(watched)
      delete_!
      other.save
      Some(other)
    } else None

  def mark_previous {
    find_all_for_the_series.filter(e => (e.season == season && e.number < number) || e.season < season).foreach(e => e.update_watched(true))
  }

  def mark_next {
    find_all_for_the_series.filter(e => (e.season == season && e.number > number) || e.season > season).foreach(e => e.update_watched(false))
  }

  def find_all_for_the_series:List[Episode] = Episode.find_by_series_id(series_id)
}

object Episode extends Episode with LongKeyedMetaMapper[Episode] with CRUDify[Long, Episode] {
  def find_by_series_id(id: Long): List[Episode] = findAll(By(Episode.series_id, id))
  def find_by_id(id: Long): Box[Episode] = find(By(Episode.episode_id, id))
  def find_unwatched: List[Episode] = findAll(By(Episode.watched, false))
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
    episode.watched(false)
    episode
  }
}