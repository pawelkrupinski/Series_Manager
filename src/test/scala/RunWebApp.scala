package net

import _root_.org.mortbay.jetty.Connector
import _root_.org.mortbay.jetty.Server
import _root_.org.mortbay.jetty.webapp.WebAppContext
import org.mortbay.jetty.nio._
import actors.Actor
import com.google.inject.Inject
import pawel.injection.{Server_Config, Injected}

object RunWebApp extends Injected {
  val server = new Server
  var started = false

  @Inject
  var serverConfig: Server_Config = _

  def stop_server = {
    server.stop()
    server.join()
  }

  def start_server {
    if (started) {
      return
    }
    started = true
    val scc = new SelectChannelConnector
    scc.setPort(serverConfig.port)
    server.setConnectors(Array(scc))

    val context = new WebAppContext()
    context.setServer(server)
    context.setContextPath("/")
    context.setWar("src/main_page/webapp")

    server.addHandler(context)

    server.start()
  }
}
