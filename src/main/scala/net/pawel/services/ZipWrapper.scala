package net.pawel.services

import com.google.common.io.CharStreams
import java.io.{Closeable, InputStreamReader, BufferedInputStream, InputStream}
import java.util.zip.{ZipInputStream, ZipEntry}

class ZipWrapper(val stream: InputStream) {
  val zipStream: ZipInputStream  = new ZipInputStream(new BufferedInputStream(stream))

  def findByFileName(name: String) = findEntry(_.getName == name)

  def findEntry(entryPredicate: ZipEntry => Boolean): String = {
    withCloseable(zipStream) {
      zipStream.find(entryPredicate) match {
        case Some(_) => CharStreams.toString(new InputStreamReader(zipStream))
        case None => null
      }
    }
  }

  def withCloseable[T](closeable: Closeable)(operation: => T): T = {
    try {
      return operation
    } finally {
      closeable.close
    }
  }

  implicit def zipInputStreamToIterator(zipInputStream: ZipInputStream): Iterator[ZipEntry]
      = new ZipWrapper.ZipEntryIterator(zipInputStream)
}

object ZipWrapper {
  def apply(stream: InputStream) = new ZipWrapper(stream)

  class ZipEntryIterator(inputStream: ZipInputStream) extends Iterator[ZipEntry] {
    var zipEntry: ZipEntry = null

    def hasNext = {
      zipEntry = inputStream.getNextEntry
      zipEntry != null
    }

    def next() = zipEntry
  }

  class ZipIterable(inputStream: ZipInputStream) extends Iterable[ZipEntry] {
    def iterator = new ZipEntryIterator(inputStream)
  }
}