package net

import _root_.org.mortbay.jetty.Connector
import _root_.org.mortbay.jetty.Server
import _root_.org.mortbay.jetty.webapp.WebAppContext
import org.mortbay.jetty.nio._
import actors.Actor

object RunWebApp {
  val server = new Server
  var started = false

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
    scc.setPort(8081)
    server.setConnectors(Array(scc))

    val context = new WebAppContext()
    context.setServer(server)
    context.setContextPath("/")
    context.setWar("src/main/webapp")

    server.addHandler(context)

    server.start()
  }
}
