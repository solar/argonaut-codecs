package org.sazabi.argonaut.codecs.spire.math

import argonaut._
import org.sazabi.argonaut.codecs.Disjunction.decoder
import scalaz.\/
import spire.math.Rational

trait Rationals {
  implicit val RationalDecodeJson: DecodeJson[Rational] = decoder[Rational] { j =>
    j.number.map(number2Rational).orElse(j.string.map(string2Rational))
      .map(_.leftMap(e => s"Rational(${e.getMessage})"))
      .getOrElse(\/.left("Rational(invalid type)"))
  }

  implicit val RationalEncodeJson: EncodeJson[Rational] = EncodeJson { r =>
    Json.jString(r.toString)
  }

  private[this] val number2Rational: JsonNumber => Throwable \/ Rational = n =>
    \/.fromTryCatchNonFatal(Rational(n.toBigDecimal))

  private[this] val string2Rational: String => Throwable \/ Rational = s =>
    \/.fromTryCatchNonFatal(Rational(s))
}
