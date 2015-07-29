package org.sazabi.argonaut.codecs.spire.math

import argonaut._, Json._, JsonIdentity._
import scalaprops._, Property.forAll
import scalaz._, std.string._
import spire.math.Rational
import spire.syntax.literals._

object RationalsTest extends Scalaprops with Rationals {
  private[this] val dj = RationalDecodeJson
  private[this] val ej = RationalEncodeJson

  private[this] def ok[A](a: A) = DecodeResult.ok(a)

  private[this] implicit val gen: Gen[Rational] = for {
    n <- Gen.choose(0, 10000)
    d <- Gen.elements(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192)
  } yield Rational(n, d)

  val DecodeJson = {
    val jsonNumber = forAll { (r: Rational) =>
      val d = r.numerator.toDouble / r.denominator.toDouble
      val result = dj.decode(jNumberOrNull(d).hcursor)

      result == ok(r)
    }.toProperties("json number")

    val jsonWholeNumber = forAll { (n: Long) =>
      dj.decode(jNumber(n).hcursor) == ok(Rational(n))
    }(Gen.positiveLong).toProperties("json whole number")

    val jsonNumberString = forAll { (r: Rational) =>
      val d = r.numerator.toDouble / r.denominator.toDouble
      val result = dj.decode(jString(d.toString).hcursor)

      result == ok(r)
    }.toProperties("json number string")

    val jsonRationalString = forAll { (r: Rational) =>
      dj.decode(jString(r.toString).hcursor) == ok(r)
    }.toProperties("json rational string")

    val jsonInvalidString = forAll { (s: String) =>
      dj.decode(jString(s).hcursor).toOption.isEmpty
    }(Gen.alphaString).toProperties("json invalid alphabet string")

    val failjsons = Gen.elements[Json](jNull, jBool(false), jBool(true),
      jArray(jString("foo") :: Nil), jArray(jNumber(1L) :: Nil),
      jSingleObject("foo", jString("bar")))

    val invalidJson = forAll { (j: Json) =>
      dj.decode(j.hcursor).toOption.isEmpty
    }(failjsons).toProperties("invalid json types")

    Properties.list(
      jsonNumber,
      jsonWholeNumber,
      jsonNumberString,
      jsonRationalString,
      jsonInvalidString
    )
  }

  // @TODO need more
  val EncodeJson = Property.prop {
    ej.encode(r"1/3") == jString("1/3") &&
      ej.encode(Rational("0.0001")) == jString("1/10000") &&
      ej.encode(Rational(BigDecimal(0.0001d))) == jString("1/10000") &&
      ej.encode(Rational(100L)) == jString("100") &&
      ej.encode(Rational(-100L)) == jString("-100")
  }.toProperties("encode")

  val Invertible = {
    forAll { (r: Rational) =>
      r.jencode.jdecode[Rational] == ok(r)
    }.toProperties("invertible")
  }
}
