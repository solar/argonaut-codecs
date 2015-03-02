package org.sazabi.argonaut.codecs.spire.math

import argonaut._
import org.sazabi.argonaut.codecs.Disjunction.decoder
import scalaz.\/
import spire.math.ULong

trait ULongs {
  implicit val ULongDecodeJson: DecodeJson[ULong] = decoder[ULong] { j =>
    j.number.map(number2ULong).orElse(j.string.map(string2ULong))
      .map(_.leftMap(e => s"ULong(${e.getMessage})"))
      .getOrElse(\/.left("ULong(invalid type)"))
  }

  implicit val ULongEncodeJson: EncodeJson[ULong] = EncodeJson { u =>
    Json.jString(u.toString)
  }

  private[this] val number2ULong: JsonNumber => Throwable \/ ULong = n => {
    n.toBigInt.map(i => \/.fromTryCatchNonFatal(ULong.fromBigInt(i)))
      .getOrElse(\/.left(new IllegalArgumentException("not an integer")))
  }

  private[this] val string2ULong: String => Throwable \/ ULong = s =>
    \/.fromTryCatchNonFatal(ULong(s))
}
