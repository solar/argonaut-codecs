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

  def dec(json: Json)(u: ULong) = d.decode(json.hcursor) shouldBe DecodeResult.ok(u)

  def fail(json: Json) = inside(d.decode(json.hcursor).toDisjunction) {
    case -\/(e) =>
  }

  "ULongDecodeJson" should "decode number to ULong" in {
    // JsonDouble
    dec(jNumberOrNull(1.0d))(ULong("1"))
    dec(jNumberOrNull(12345.0d))(ULong("12345"))
    dec(jNumberOrNull(Long.MaxValue.toDouble))(
      ULong(BigDecimal(Long.MaxValue.toDouble).toBigInt.toString))
    dec(jNumberOrNull(Long.MaxValue.toDouble + 1d))(
      ULong(BigDecimal(Long.MaxValue.toDouble + 1).toBigInt.toString))

    // JsonLong
    dec(jNumberOrNull(0L))(ULong.fromLong(0L))
    dec(jNumberOrNull(12345L))(ULong.fromLong(12345L))
    dec(jNumberOrNull(Long.MaxValue))(ULong.fromLong(Long.MaxValue))

    // JsonBigDecimal
    dec(jNumberOrNull(BigDecimal("0")))(ULong("0")) 
    dec(jNumberOrNull(BigDecimal("10000000")))(ULong("10000000")) 
    val max = Long.MaxValue.toString
    dec(jNumberOrNull(BigDecimal(max)))(ULong(max))

    // overflow, truncated
    val over = "1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
    dec(jNumberOrNull(BigDecimal(over)))(ULong(0L))
  }

  it should "fail to decode invalid number" in {
    fail(jNumberOrNull(123.45d))
    fail(jNumberOrNull(-123d))
    fail(jNumberOrNull(-1L))
    fail(jNumberOrNull(BigDecimal("0.1")))
    fail(jNumberOrNull(BigDecimal("-123")))
  }

  it should "decode string to ULong" in {
    dec(jString("0"))(ULong(0L))
    dec(jString("1"))(ULong(1L))
    dec(jString(Long.MaxValue.toString))(ULong(Long.MaxValue))

    // overflow, truncated
    val over = "1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
    dec(jString(over))(ULong(0L))
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
