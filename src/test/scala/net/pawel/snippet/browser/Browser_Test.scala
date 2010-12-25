package net.pawel.snippet.browser

import com.novocode.junit.TestMarker

trait Browser_Test extends TestMarker {
  def wait_for(condition: () => Unit) {
    val time: Long = System.currentTimeMillis
    while (true && (System.currentTimeMillis - time) < 1000 * 10) {
      try {
        condition.apply
        return
      } catch {
        case _ => Thread.sleep(100)
      }
    }
  }
}