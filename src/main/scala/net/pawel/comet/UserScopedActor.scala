package net.pawel.comet

import net.liftweb.http.CometActor

trait UserScopedActor extends CometActor {
  var userId: Long = _

  override protected def localSetup() {
    super.localSetup()
    val params: Array[String] = name.open_!.split(':')
    userId = params(0).toLong
  }
}



