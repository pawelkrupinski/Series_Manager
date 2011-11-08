package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.Episode_Fetching

class Unwatched extends CometActor with Episode_Fetching with Episode_Binding_Comet with Logger {

  var userId: Long = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    userId = params(0).toLong
  }

  def render = ".episodes *" #> bindEpisodesCss(unwatched_episodes, userId)
}




