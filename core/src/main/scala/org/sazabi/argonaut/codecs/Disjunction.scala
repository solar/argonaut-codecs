package org.sazabi.argonaut.codecs

import argonaut._
import scalaz._

object Disjunction {
  def decoder[A](f: Json => String \/ A): DecodeJson[A] = DecodeJson { a =>
    f(a.focus).fold(DecodeResult.fail(_, a.history), DecodeResult.ok)
  }
}
