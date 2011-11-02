package net.pawel.comet

import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.{Updated_Watched, Episode_Fetching}

class Unaired extends CometActor with Episode_Fetching with Episode_Binding_Comet with Logger {
  def render = ".episodes *" #> bindEpisodesCss(unaired_episodes)
}




