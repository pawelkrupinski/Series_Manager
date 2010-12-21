package net.pawel.services

import net.pawel.model.{Episode, Series}
import java.io.StringReader
import java.lang.String
import java.util.Date
import net.pawel.injection.Injected
import com.google.inject.Inject
import xml.{Elem, NodeSeq, XML}

object Series_Service extends Injected {

  @Inject
  var http: Http = _

  val tvdb_service = "http://www.thetvdb.com/api";
  val tvdb_service_with_key = tvdb_service + "/1D35F19D7E41B952";

  def fetch_episodes(series: Series) = {
    val xml = XML.load(new StringReader(ZipWrapper(http.get(tvdb_service_with_key + "/series/" + series.series_id + "/all/en.zip")).findByFileName("en.xml")))
    (for (nodes <- (xml \\ "Episode")) yield Episode.from(nodes)).toList
  }

  def fetch_episode(episode_id: Long) = Episode.from(http.urlToXml(tvdb_service_with_key + "/episodes/" + episode_id + "/") \\ "Episode")

  def create_series(series: Series) {
    if (Series.id_exists(series.series_id)) {
      throw new IllegalArgumentException("Series " + series.series_id + " already exists.")
    }
    series.save
    add_episodes(series)
  }

  private def add_episodes(series: Series) = fetch_episodes(series).foreach(_.save)

  def find_series(name: String): List[Series] = {
    val url: String = tvdb_service + "/GetSeries.php?seriesname=" + name.replace(" ", "+")
    val xml = http.urlToXml(url)
    (xml \\ "Series").map(Series.from(_)).toList
  }
}