package net.pawel.services

import com.google.common.io.CharStreams
import java.io._
import java.util.zip.{ZipInputStream, ZipEntry}
import xml.{Source, XML}

object TV {
  def main(args: Array[String]) {
    println(classOf[ZipInputStream].getResourceAsStream("/net/pawel/services/en.xml"))
    println(Source.fromFile("net/pawel/services/en.xml"))
    //val series = Http.get("http://www.thetvdb.com/api/1D35F19D7E41B952/series/75760/all/en.zip")
    //val entry = new ZipIterable(series).find(_.getName == "en.xml").get

    //System.out.println(ZipWrapper(series).findEntry(_.getName == "en.xml"))
    
//    val series2 = Http.urlToString("http://www.thetvdb.com/api/GetSeries.php?seriesname=How%20I%20Met%20Your%20Mother")
//    val xml = XML.load(new StringReader(series2))
//    System.out.println(xml \\ "Series")
  }
}

