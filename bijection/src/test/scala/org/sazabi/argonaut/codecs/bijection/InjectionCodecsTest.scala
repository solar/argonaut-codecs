package org.sazabi.argonaut.codecs.bijection

import argonaut._, Json._
import com.twitter.bijection._
import scala.util.{ Failure, Success }
import scalaz._, std.string._
import scalaprops._, Property.forAll

object InjectionCodecsTest extends Scalaprops with InjectionCodecs {
  private[this] case class A(str: String)

  private[this] object A {
    val ValidChars = ('A' to 'Z').toSet

    implicit val inj: Injection[A, String] = Injection.build[A, String](_.str) { str =>
      if (str.nonEmpty && str.forall(c => ValidChars(c))) Success(A(str))
      else Failure(new IllegalArgumentException)
    }

    implicit val gen: Gen[A] = Gen.parameterised { (size, r) =>
      Gen.chooseR(1, size.max(1), r).flatMap { i =>
        Gen.sequenceNList[Char](i, Gen.alphaUpperChar).map { lst =>
          A(lst.mkString(""))
        }
      }
    }
  }

  val injectionToDecodeJson = {
    implicit val dj = InjectionToDecodeJson[A, String]

    val p1 = forAll { (s: String) =>
      val result = jString(s).jdecode[A].toOption
      if (s.nonEmpty) result == Some(A(s))
      else result.isEmpty
    }(Gen.alphaUpperString).toProperties("valid string")

    val p2 = forAll { (s: String) =>
      jString(s).jdecode[A].toOption == None
    }(Gen.alphaLowerString).toProperties("invalid string")

    Properties.list(p1, p2)
  }

  val injectionToEncodeJson = {
    implicit val ej = InjectionToEncodeJson[A, String]

    forAll { (a: A) =>
      ej.encode(a) == jString(a.str)
    }.toProperties("encode")
  }
}
