package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.Episode_Fetching

class Recently_Aired extends CometActor with Episode_Fetching with Episode_Binding_Comet with Logger {
  override protected def dontCacheRendering = true

  def render = {
    debug("Rendering. Episodes: " + episodes)
    ".episodes *" #> bindEpisodesCss(recently_aired_episodes)
  }
}

