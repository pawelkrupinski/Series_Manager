package net.pawel.snippet.browser

import com.novocode.junit.TestMarker
import junit.framework.AssertionFailedError

trait Browser_Test extends TestMarker {
  def wait_for[T](condition: () => T): T = {
    val time: Long = System.currentTimeMillis
    while (true && (System.currentTimeMillis - time) < 1000 * 10) {
      try {
        return condition.apply
      } catch {
        case _ => Thread.sleep(100)
      }
    }
    throw new AssertionFailedError("Wait has timed out")
  }
}