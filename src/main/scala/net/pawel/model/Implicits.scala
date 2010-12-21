package net.pawel.model

import net.liftweb.mapper._
import xml.NodeSeq
import java.text.{ParseException, SimpleDateFormat}
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.Provider

object Implicits {
  implicit def pimpMyXml(xml: NodeSeq) = new {
    def apply(name: String): String = (xml \\ name).text
    def long(name: String) = apply(name).toLong
    def int(name: String) = apply(name).toInt
    def date(name: String) = try {(new SimpleDateFormat("yyyy-MM-dd")).parse(apply(name))} catch {case e: ParseException => null}
  }

  implicit def pimpMyBuilder[T](builder: AnnotatedBindingBuilder[T]) = new {
    def to_provider(provider: () => T) = builder.toProvider(new Provider[T] {
      def get = provider.apply
    })
  }

  implicit val episode_ordering = new Ordering[Episode] {
    def value(episode: Episode) = episode.season * 1000 + episode.number

    def compare(x: Episode, y: Episode): Int = {
      val result = x.series.name.compareTo(y.series.name)
      if (result != 0) return result
      value(x) - value(y)
    }
  }
}







