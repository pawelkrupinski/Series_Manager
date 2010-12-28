package net.pawel.snippet.browser

import com.novocode.junit.TestMarker
import junit.framework.AssertionFailedError
import org.junit.After
import net.pawel.injection.Server_Config
import org.openqa.selenium.WebDriver
import com.google.inject.Inject

trait Browser_Test extends TestMarker {

  @Inject
  var driver: WebDriver = _

  @Inject
  var server_config: Server_Config = _

  @After
  def close_browser {
    try {
      driver.close
    } catch {
     case _ =>
    }

  }

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