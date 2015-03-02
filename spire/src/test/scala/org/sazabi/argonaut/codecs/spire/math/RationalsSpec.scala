package org.sazabi.argonaut.codecs.spire.math

import argonaut._, Json._
import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest._
import scalaz._
import spire.math.Rational
import spire.syntax.literals._

class RationalsSpec extends FlatSpec with Matchers with Inside with prop.PropertyChecks with Rationals {
  val d = RationalDecodeJson
  val e = RationalEncodeJson

  implicit val arb: Arbitrary[Rational] = Arbitrary(for {
    n <- Gen.choose(0, 10000)
    d <- Gen.choose(1, 10000)
  } yield Rational(n, d))

  "RationalDecodeJson" should "decode numbers to Rational" in {
    inside(d.decode(jNumberOrNull(0.1d).hcursor).toDisjunction) {
      case \/-(r) => r shouldBe r"1/10"
    }

    inside(d.decode(jNumberOrNull(0.00000000001d).hcursor).toDisjunction) {
      case \/-(r) => r shouldBe r"1/100000000000"
    }

    inside(d.decode(jNumberOrNull(16).hcursor).toDisjunction) {
      case \/-(r) => r shouldBe r"16"
    }

    inside(d.decode(jNumberOrNull(0.33333d).hcursor).toDisjunction) {
      case \/-(r) => r shouldBe r"33333/100000"
    }

    inside(d.decode(jNumberOrNull(-1).hcursor).toDisjunction) {
      case \/-(r) => r shouldBe r"-1"
    }
  }

  it should "decode strings to Rational" in {
    inside(d.decode(jString("0.333").hcursor).toDisjunction) {
      case \/-(r) => r shouldBe r"333/1000"
    }

    inside(d.decode(jString("1/3").hcursor).toDisjunction) {
      case \/-(r) => r shouldBe r"1/3"
    }

    inside(d.decode(jString("foo").hcursor).toDisjunction) {
      case -\/(e) =>
    }
  }

  it should "fail to decode other types to Json" in {
    inside(d.decode(jNull.hcursor).toDisjunction) {
      case -\/(e) =>
    }

    inside(d.decode(jBool(true).hcursor).toDisjunction) {
      case -\/(e) =>
    }

    inside(d.decode(jBool(false).hcursor).toDisjunction) {
      case -\/(e) =>
    }

    inside(d.decode(jArray(jString("foo") :: Nil).hcursor).toDisjunction) {
      case -\/(e) =>
    }

    inside(d.decode(jSingleObject("foo", jString("bar")).hcursor).toDisjunction) {
      case -\/(e) =>
    }
  }

  "RationalEncodeJson" should "encode Rational to Json" in {
    e.encode(r"1/3") shouldBe jString("1/3")
    e.encode(Rational("0.0001")) shouldBe jString("1/10000")
    e.encode(Rational(BigDecimal(0.0001d))) shouldBe jString("1/10000")
    e.encode(Rational(100L)) shouldBe jString("100")
    e.encode(Rational(-100L)) shouldBe jString("-100")
  }

  "RationalDecodeJson and RationalEncodeJson" should "be invertible" in {
    forAll { (r: Rational) =>
      inside(d.decode(e.encode(r).hcursor).toDisjunction) {
        case \/-(v) => v shouldBe r
      }
    }
  }
}
