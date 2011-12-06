package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.pawel.lib.Episode_Fetching
import net.pawel.model.Episode

trait HomePageEpisodes extends CometActor with Episode_Fetching with Episode_Binding_Comet with Logger with UserScopedActor {
  def episodes_to_render: List[Episode]

  def render = ".episodes *" #> bindEpisodesCss(episodes_to_render, userId)
}






