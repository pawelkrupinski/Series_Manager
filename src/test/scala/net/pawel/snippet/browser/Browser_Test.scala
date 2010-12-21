package net.pawel.snippet.browser

trait Browser_Test {
  def wait_for(condition: () => Unit) {
    val time: Long = System.currentTimeMillis
    while (true && (System.currentTimeMillis - time) < 1000 * 10) {
      try {
        condition.apply
        return
      } catch {
        case _ => Thread.sleep(500)
      }
    }
  }
}