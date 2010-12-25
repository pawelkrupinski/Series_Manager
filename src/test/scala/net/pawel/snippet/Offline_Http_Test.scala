package net.pawel.snippet

import net.pawel.services.Offline_Http
import org.hamcrest.CoreMatchers._
import org.junit.{Assert, Test}
import java.io.{InputStreamReader, BufferedReader, InputStream}
import com.google.common.io.CharStreams
import com.novocode.junit.TestMarker

class Offline_Http_Test extends TestMarker {

  @Test
  def Get_Series {
    val result: InputStream = Offline_Http.get("GetSeries?rome")
    Assert.assertThat(result, is(not(nullValue[InputStream])));
    val content: String = CharStreams.toString(new BufferedReader(new InputStreamReader(result)))
    println(content)
  }

  @Test
  def Zip {
    val result: InputStream = Offline_Http.get("en.zip")
    Assert.assertThat(result, is(not(nullValue[InputStream])));
    val content: String = CharStreams.toString(new BufferedReader(new InputStreamReader(result)))
    println(content)
  }

}