package net.pawel.snippet

import net.liftweb.util.Helpers._
import xml._
import net.liftweb.http.S
import net.pawel.model.User
import net.liftweb.common.Logger

class List_Seasons_Snippet extends Logger {
  def render = ".replace_comet" #> addComet _

  def addComet(in: NodeSeq): NodeSeq = in match { case elem: Elem => (elem % ("class" -> liftComet("List_Seasons", cometActorName))) }
  def liftComet(className: String, name: String) = "lift:comet?type=" + className + "?eager_eval=true?name=" + name;
  def cometActorName = userId + ":" + S.param("series_id").open_!
  def userId = User.currentUser.open_!.id.toString()

  def main = ".replace_comet" #> addCometMain _

  def addCometMain(in: NodeSeq): NodeSeq = in match {
    case elem: Elem => (elem % ("class" -> (elem.attribute("comet").head + "?name=" + userId)))
  }
}

