package net.pawel.services

import com.google.common.io.CharStreams
import xml.XML
import io.BufferedSource
import java.lang.String
import java.io.{StringReader, InputStream, BufferedReader, InputStreamReader}

trait Http {
  def get(urlString: String): InputStream

  def urlToXml(url: String) = XML.load(get(url))
}

object Offline_Http extends Http {
  def get(urlString: String): InputStream = if (urlString.contains("GetSeries")) {
      if (urlString.toLowerCase.contains("earth")) {
        classOf[ZipWrapper].getResourceAsStream("/GetSeries-Earth.xml")
      } else if (urlString.toLowerCase.contains("rome")) {
        classOf[ZipWrapper].getResourceAsStream("/GetSeries-Rome.xml")
      } else null
    } else if (urlString.contains("en.zip")) {
      classOf[ZipWrapper].getResourceAsStream("/en-Earth.xml")
    } else {
      null
    } match {
      case null => throw new RuntimeException("Stream for url " + urlString + " is null.")
      case stream => stream
    }
}

object Http extends Http {
  import java.net.URL;

  def get(urlString: String): InputStream = {
    try {
      new URL(urlString).openStream
    } catch {
      case _ => null
    }
  }
}