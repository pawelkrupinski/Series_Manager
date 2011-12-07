package net.pawel.comet

import net.liftweb.http.CometActor
import net.pawel.lib.Registers_User

trait UserScopedActor extends CometActor with Registers_User {
  var userId: Long = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    userId = params(0).toLong
    register(userId)
  }

  override protected def localShutdown() {
    unregister(userId)
  }
}



