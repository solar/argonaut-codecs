package org.sazabi.argonaut.codecs

import argonaut._
import scalaz._

object Disjunction {
  def decoder[A](f: Json => String \/ A): DecodeJson[A] = DecodeJson { a =>
    f(a.focus).fold(DecodeResult.fail(_, a.history), DecodeResult.ok)
  }

  def codec[A, B](to: A => B, from: B => String \/ A)(implicit e: EncodeJson[B], d: DecodeJson[B]): CodecJson[A] =
    CodecJson(a => e.encode(to(a)), { a =>
      for {
        b <- d.decode(a)
        r <- from(b).fold(str => DecodeResult.fail(str, a.history),
          DecodeResult.ok(_))
      } yield r
    })
}
