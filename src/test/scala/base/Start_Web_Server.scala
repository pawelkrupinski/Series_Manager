package base

import net.pawel.injection.Integration_Configuration
import org.junit.{Before, After}
import bootstrap.liftweb.Boot
import net.RunWebApp

trait Start_Web_Server {
  @Before
  def start_server {
    RunWebApp.start_server
    Boot.schemify
  }

  @After
  def stop_server {
    Boot.destroy
  }
}