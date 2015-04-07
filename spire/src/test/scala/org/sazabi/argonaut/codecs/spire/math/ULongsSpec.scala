package org.sazabi.argonaut.codecs.spire.math

import argonaut._, Json._
import org.scalatest._
import scalaz._
import spire.math.ULong
import org.scalacheck.Arbitrary

class ULongsSpec extends FlatSpec with Matchers with Inside with ULongs with prop.PropertyChecks {
  val d = ULongDecodeJson
  val e = ULongEncodeJson

  implicit def arb(implicit arbLong: Arbitrary[Long]): Arbitrary[ULong] =
    Arbitrary(arbLong.arbitrary.map(ULong.fromLong))

  // def dec(json: Json)(u: ULong) = d.decode(json.hcursor) shouldBe DecodeResult.ok(u)

  def dec(json: Json) = d.decode(json.hcursor)

  def ok[A](a: A) = DecodeResult.ok(a)

  def fail(json: Json) = inside(d.decode(json.hcursor).toDisjunction) {
    case -\/(e) =>
    case \/-(v) => println(v)
  }

  "ULongDecodeJson" should "decode number to ULong" in {
    // JsonDouble
    dec(jNumberOrNull(1.0d)) shouldBe ok(ULong("1"))
    dec(jNumberOrNull(12345.0d)) shouldBe ok(ULong("12345"))

    dec(jNumberOrNull(Long.MaxValue.toDouble)) shouldBe ok(
      ULong.fromLong(Long.MaxValue))

    // doulbe <> long
    dec(jNumberOrNull(Long.MaxValue.toDouble + 1d)) should not be ok(
      ULong((BigDecimal(Long.MaxValue) + 1).toString))

    // JsonLong
    dec(jNumberOrNull(0L)) shouldBe ok(ULong.fromLong(0L))
    dec(jNumberOrNull(12345L)) shouldBe ok(ULong.fromLong(12345L))

    dec(jNumber(Long.MaxValue)) shouldBe ok(ULong.fromLong(Long.MaxValue))

    // JsonBigDecimal
    dec(jNumber(BigDecimal("0"))) shouldBe ok(ULong("0")) 
    dec(jNumber(BigDecimal("10000000"))) shouldBe ok(ULong("10000000")) 
    val max = Long.MaxValue.toString
    dec(jNumber(BigDecimal(max))) shouldBe ok(ULong(max))

    // overflow, truncated
    val over = "1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
    dec(jNumber(BigDecimal(over))) shouldBe ok(ULong(0L))
  }

  it should "fail to decode invalid number" in {
    fail(jNumberOrNull(123.45d))
    fail(jNumberOrNull(-123d))
    fail(jNumber(-1L))
    fail(jNumber(BigDecimal("0.1")))
    fail(jNumber(BigDecimal("-123")))
  }

  it should "decode string to ULong" in {
    dec(jString("0")) shouldBe ok(ULong(0L))
    dec(jString("1")) shouldBe ok(ULong(1L))
    dec(jString(Long.MaxValue.toString)) shouldBe ok(ULong(Long.MaxValue))

    // overflow, truncated
    val over = "1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
    dec(jString(over)) shouldBe ok(ULong(0L))
  }

  it should "fail to decode invalid string" in {
    fail(jString("1.5"))
    fail(jString("-1"))
  }

  "ULongEncodeJson" should "encode ULong to Json" in {
    e.encode(ULong(0L)) shouldBe jString("0")
    e.encode(ULong(10000000L)) shouldBe jString("10000000")
    e.encode(ULong(-1L)) shouldBe jString("18446744073709551615")
  }

  "ULongDecodeJson and ULongEncodeJson" should "be invertible" in {
    forAll { (u: ULong) =>
      inside(d.decode(e.encode(u).hcursor).toDisjunction) {
        case \/-(v) => v shouldBe u
      }
    }
  }
}
