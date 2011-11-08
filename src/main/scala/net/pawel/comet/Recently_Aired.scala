package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.{Remove_Listener, Add_Listener, Episode_Manager, Episode_Fetching}

class Recently_Aired extends CometActor with Episode_Fetching with Episode_Binding_Comet with Logger {

  var userId: Long = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    userId = params(0).toLong
  }

  def render = {
    debug("Rendering. Episodes: " + episodes)
    ".episodes *" #> bindEpisodesCss(recently_aired_episodes, userId)
  }
}

