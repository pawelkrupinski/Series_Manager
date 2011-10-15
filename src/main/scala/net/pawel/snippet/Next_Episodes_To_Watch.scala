package net.pawel.snippet

import main_page.Episode_Binding
import xml.NodeSeq
import java.util.Date
import org.joda.time.DateTime
import net.pawel.model.Implicits._
import net.pawel.model.Episode
import net.liftweb.util.Helpers._

class Next_Episodes_To_Watch extends Episode_Binding {
  def render(in: NodeSeq) = {
    val episodes: List[Episode] = Episode.find_unwatched.groupBy(_.series_id.toLong).iterator
      .map((t: (Long, List[Episode])) => t._2.sortBy(e => e.season * 1000 + e.number).take(2)).toList.flatten

    val (unaired, unwatched) = episodes.partition(e => e.aired.toString == "NULL" || e.aired.compareTo(new Date) > 0)
    val (old_unwatched, recently_aired) = unwatched.partition(_.aired.compareTo((new DateTime).minusMonths(1).toDate) < 0)

    bind("episodes", in,
      "unwatched" -> bindEpisodes(old_unwatched.sorted) _,
      "unaired" -> bindEpisodes(unaired.sortWith((e1, e2) => if (e1.aired.toString == "NULL") false
        else if (e2.aired.toString == "NULL") true
        else e1.aired.compareTo(e2.aired) < 0)) _)
  }
}