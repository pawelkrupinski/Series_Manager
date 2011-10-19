package net.pawel.snippet

import main_page.{Episode_Binding, Episode_Fetching}
import net.liftweb.util.Helpers._

class Unwatched extends Episode_Fetching with Episode_Binding {
  def render = ".episodes *" #> bindEpisodesCss(unwatched_episodes)
}




