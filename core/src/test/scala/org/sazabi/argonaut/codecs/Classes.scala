package org.sazabi.argonaut.codecs

import scalaz._, Isomorphism._

sealed trait Opt[A] {
  def isOne: Boolean
}

case class One[A](value: A) extends Opt[A] {
  val isOne = true
}

case class Zero[A]() extends Opt[A] {
  val isOne = false
}

object Opt {
  implicit val maybeFunctorIso: (Maybe <~> Opt) = {
    new IsoFunctorTemplate[Maybe, Opt] {
      def from[A](fa: Opt[A]): Maybe[A] = fa match {
        case One(a) => Maybe.just(a)
        case Zero() => Maybe.empty
      }

      def to[A](ga: Maybe[A]): Opt[A] = ga.cata(One(_), Zero())
    }
  }
}

case class Str(value: String)

object Str {
  implicit val stringIso: (String <=> Str) = {
    new IsoSet[String, Str] {
      val to: String => Str = Str(_)
      val from: Str => String = _.value
    }
  }
}
