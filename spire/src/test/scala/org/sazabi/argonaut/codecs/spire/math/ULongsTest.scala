package org.sazabi.argonaut.codecs.spire.math

import argonaut._, Json._, JsonIdentity._
import scalaprops._, Property.forAll
import scalaz._, std.string._
import spire.math.ULong

object ULongsTest extends Scalaprops with ULongs {
  private[this] val d = ULongDecodeJson
  private[this] val e = ULongEncodeJson

  private[this] implicit val gen: Gen[ULong] = Gen[Long].map(ULong.fromLong)

  private[this] def dec(json: Json) = d.decode(json.hcursor)

  private[this] def ok[A](a: A) = DecodeResult.ok(a)

  private[this] def fail(json: Json) = d.decode(json.hcursor).result.fold(_ => (),
    value => println(value))

  val uLongDecodeJson = {
    val jsonDouble = forAll { (d: Double) =>
      val result = dec(jNumberOrNull(d))

      if (d.isNaN) result.toOption.isEmpty
      else if (d < 0d || !d.isWhole) result.toOption.isEmpty
      else if (d > Long.MaxValue.toDouble) {
        val bi = BigDecimal(d).toBigInt
        result == ok(ULong.fromBigInt(bi))
      } else {
        result == ok(ULong.fromLong(d.toLong))
      }
    }.toProperties("json double")

    val jsonLong = forAll { (n: Long) =>
      val result = dec(jNumber(n))

      if (n >= 0) result == ok(ULong.fromLong(n))
      else result.toOption.isEmpty
    }.toProperties("json long")

    val jsonDec = forAll { (n: BigDecimal) =>
      val result = dec(jNumber(n))

      if (n < 0 || !n.isWhole) result.toOption.isEmpty
      else {
        result == ok(ULong.fromBigInt(n.toBigInt))
      }
    }.toProperties("json big decimal")

    val jsonString = forAll { (s: String) =>
      val result = dec(jString(s))
      if (s.isEmpty) result.toOption.isEmpty
      else result == ok(ULong(s))
    }(Gen.numString).toProperties("json numeric string")

    val jsonAlphaString = forAll { (s: String) =>
      dec(jString(s)).toOption.isEmpty
    }(Gen.alphaString).toProperties("json alphabet string should fail")

    Properties.list(
      jsonDouble, jsonLong, jsonDec, jsonString, jsonAlphaString
    )
  }

  val uLongEncodeJson = {
    forAll { (n: ULong) =>
      n.jencode == jString(n.toString)
    }.toProperties("encode")
  }

  val invertible = {
    forAll { (n: ULong) =>
      n.jencode.jdecode[ULong] == ok(n)
    }.toProperties("encode/decode")
  }
}
