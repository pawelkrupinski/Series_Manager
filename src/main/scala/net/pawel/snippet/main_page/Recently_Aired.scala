package net.pawel.snippet

import main_page.{Episode_Binding, Episode_Fetching}
import net.liftweb.util.Helpers._

class Recently_Aired extends Episode_Fetching with Episode_Binding {
  def render = ".episodes *" #> bindEpisodesCss(recently_aired_episodes)
}

