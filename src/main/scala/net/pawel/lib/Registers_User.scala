package net.pawel.lib

import net.liftweb.common.Logger
import net.pawel.model.{Series, Episode}
import net.liftweb.http._
import net.pawel.snippet.{Series_Updated, Update}

trait Registers_User {
  def register(userId: Long)
  def unregister(userId: Long)
}




