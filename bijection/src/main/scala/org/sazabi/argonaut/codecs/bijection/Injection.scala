package org.sazabi.argonaut.codecs.bijection

import scala.util.{ Failure, Success }

import argonaut._
import com.twitter.bijection.{ Injection, InversionFailure }
import scalaz._
import shapeless.Lazy

trait InjectionCodecs {
  def InjectionToDecodeJson[A, B](implicit inj: Lazy[Injection[A, B]],
    db: Lazy[DecodeJson[B]]): DecodeJson[A] = DecodeJson { c =>
      for {
        b <- db.value.decode(c)
        dr <- inj.value.invert(b) match {
          case Success(inverted) => DecodeResult.ok(inverted)
          case Failure(e) => DecodeResult.fail(e.getMessage, c.history)
        }
      } yield dr
    }

  def InjectionToEncodeJson[A, B](implicit inj: Lazy[Injection[A, B]],
    eb: Lazy[EncodeJson[B]]): EncodeJson[A] = EncodeJson(v => eb.value.encode(inj.value(v)))

  def InjectionToCodecJson[A, B](implicit inj: Lazy[Injection[A, B]],
    eb: Lazy[EncodeJson[B]], db: Lazy[DecodeJson[B]]): CodecJson[A] =
      CodecJson(a => eb.value.encode(inj.value(a)), { c =>
        for {
          b <- db.value.decode(c)
          dr <- inj.value.invert(b) match {
            case Success(inverted) => DecodeResult.ok(inverted)
            case Failure(e) => DecodeResult.fail(e.getMessage, c.history)
          }
        } yield dr
      })
}
