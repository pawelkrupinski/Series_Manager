package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.{Add_Listener, Remove_Listener, Episode_Manager, Episode_Fetching}

class Unwatched extends CometActor with Episode_Fetching with Episode_Binding_Comet with Logger {

  var userId: Long = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    userId = params(0).toLong
    Episode_Manager ! Add_Listener(this, userId)
  }

  override protected def localShutdown() {
    super.localShutdown()
    Episode_Manager ! Remove_Listener(this, userId)
  }

  def render = ".episodes *" #> bindEpisodesCss(unwatched_episodes, userId)
}




