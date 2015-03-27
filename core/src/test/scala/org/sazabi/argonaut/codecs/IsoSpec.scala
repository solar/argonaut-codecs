package org.sazabi.argonaut.codecs

import argonaut._
import org.scalacheck._
import org.scalatest._
import scalaz._, Isomorphism._
import shapeless._

class IsoSpec extends FunSpec with Matchers with Inside with prop.PropertyChecks {
  describe("Iso") {
    implicit val iso = Lazy(Maybe.optionMaybeIso)

    describe("fuctorDecodeJson") {
      implicit def maybeDecodeJson[A](implicit d: DecodeJson[A]) =
        Iso.functorDecodeJson[Option, Maybe, A]

      it("should create DecodeJson using Isomorphism") {
        "implicitly[DecodeJson[Maybe[Int]]]" should compile
        "implicitly[DecodeJson[Maybe[String]]]" should compile
      }
    }

    describe("functorEncodeJson") {
      implicit def maybeEncodeJson[A](implicit d: EncodeJson[A]) =
        Iso.functorEncodeJson[Option, Maybe, A]

      it("should create EncodeJson using Isomorphism") {
        "implicitly[EncodeJson[Maybe[Int]]]" should compile
        "implicitly[EncodeJson[Maybe[String]]]" should compile
      }
    }
  }
}
