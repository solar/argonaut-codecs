package org.sazabi.argonaut.codecs.bijection

import argonaut._

import com.twitter.bijection.{ Injection, InversionFailure }

import scala.util.{ Failure, Success }

trait InjectionCodecs {
  def InjectionToDecodeJson[A, B](implicit inj: Injection[A, B],
    db: DecodeJson[B]): DecodeJson[A] = DecodeJson { c =>
      for {
        b <- db.decode(c)
        dr <- inj.invert(b) match {
          case Success(inverted) => DecodeResult.ok(inverted)
          case Failure(e) => DecodeResult.fail(e.getMessage, c.history)
        }
      } yield dr
    }

  def InjectionToEncodeJson[A, B](implicit inj: Injection[A, B],
    eb: EncodeJson[B]): EncodeJson[A] = EncodeJson(v => eb.encode(inj(v)))

  def InjectionToCodecJson[A, B](implicit inj: Injection[A, B],
    eb: EncodeJson[B], db: DecodeJson[B]): CodecJson[A] =
      CodecJson(a => eb.encode(inj(a)), { c =>
        for {
          b <- db.decode(c)
          dr <- inj.invert(b) match {
            case Success(inverted) => DecodeResult.ok(inverted)
            case Failure(e) => DecodeResult.fail(e.getMessage, c.history)
          }
        } yield dr
      })
}
