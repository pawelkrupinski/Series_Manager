package net.pawel.snippet

import net.liftweb.util.Helpers._
import xml._
import net.liftweb.http.S
import net.pawel.model.{Series, User}
import net.liftweb.common.Logger

class Seasons extends Logger {
  def render = {
    val seasons: List[Int] = Series.find_by_id(S.param("series_id").open_!.toLong).open_!.episodes.map(_.season.get)
      .toSet.toList.sorted
    "li" #> seasons.map(season =>
      "li [class]" #> active(seasons.head, season) &
      "a [href]" #> ("#season" + season) &
      "a *" #> ("Season " + season)) &
    ".tab-pane" #> seasons.map(season =>
        ".tab-pane [class+]" #> active(seasons.head, season) &
        "div [id]" #> ("season" + season) &
        ".replace_comet [class]" #> addComet(season)
      )

  }

  def active(first: Int, season: Int) = if (first == season) "active" else ""

  def addComet(season: Int) = liftComet("List_Episodes", cometActorName(season))
  def liftComet(className: String, name: String) = "lift:comet?type=" + className + "?eager_eval=true?name=" + name;
  def cometActorName(season: Int) = userId + ":" + S.param("series_id").open_! + ":" + season
  def userId = User.currentUser.open_!.id.toString()
}