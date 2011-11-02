package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.{Episode_Fetching, Updated_Watched}

class Recently_Aired extends CometActor with Episode_Fetching with Episode_Binding_Comet with Logger {
  override protected def dontCacheRendering = true

  def render = ".episodes *" #> bindEpisodesCss(recently_aired_episodes)
  
  override def lowPriority = {
    case Updated_Watched(from, to) => {
      debug("Updated watched received.")
      reRender()
    };
  }
}

