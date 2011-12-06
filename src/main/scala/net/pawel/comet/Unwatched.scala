package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.Episode_Fetching

class Unwatched extends CometActor with UserScopedActor with Episode_Fetching with Episode_Binding_Comet with Logger {
  def render = ".episodes *" #> bindEpisodesCss(unwatched_episodes, userId)
}




