package net.pawel.comet

import net.pawel.snippet.main_page.Episode_Fetching

class Recently_Aired extends Episode_Fetching with Episode_Binding_Comet {
  override protected def dontCacheRendering = true

  def render = ".episodes *" #> bindEpisodesCss(recently_aired_episodes)
}

