package net.pawel.snippet

import net.liftweb.util.Helpers._
import xml._
import net.liftweb.http.S
import net.pawel.model.User

class List_Episodes_Snippet {
  def render = ".replace_comet" #> addComet _

  def addComet(in: NodeSeq): NodeSeq = in match { case elem: Elem => (elem % ("class" -> liftComet)) }
  def liftComet = "lift:comet?type=List_Episodes?name=" + cometActorName +  "?eager_eval=true"
  def cometActorName = User.currentUser.open_!.id + ":" + S.param("series_id").open_! + ":" + S.param("season").open_!
}

