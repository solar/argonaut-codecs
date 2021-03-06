package org.sazabi.argonaut.codecs

import argonaut._, Json._, JsonIdentity._
import scalaprops._, Property.forAll
import scalaz._, Isomorphism.{ <~>, IsoFunctorTemplate }, std.string._
import shapeless._

object IsoTest extends Scalaprops {
  val functorDecodeJson = {
    implicit val iso = Opt.maybeFunctorIso // for scala 2.10

    implicit val dj = Iso.functorDecodeJson[Maybe, Opt, String]

    forAll { (maybe: Maybe[String @@ GenTags.Ascii]) =>
      val json = maybe.cata(v => jString(Tag.unwrap(v)), jNull)

      val opt = json.jdecode[Opt[String]].toOption.get

      (maybe, opt) match {
        case (Maybe.Just(a), One(b)) => a == b
        case (Maybe.Just(a), Zero()) => false
        case (Maybe.Empty(), One(b)) => false
        case (Maybe.Empty(), Zero()) => true
      }
    }
  }

  val functorEncodeJson = {
    implicit val iso = Opt.maybeFunctorIso // for scala 2.10
    implicit val ej = Iso.functorEncodeJson[Maybe, Opt, String]

    forAll { (maybe: Maybe[String @@ GenTags.Ascii]) =>
      val opt = Opt.maybeFunctorIso.to(maybe.map(Tag.unwrap))

      val json = ej.encode(opt)

      json.string == maybe.toOption && json.isNull == maybe.isEmpty
    }
  }

  val decodeJson = {
    implicit val dj = Iso.decodeJson[String, Str]

    val id = forAll { (v: String @@ GenTags.Ascii) =>
      val s = Tag.unwrap(v)
      val json = jString(s)

      json.jdecode[Str].toDisjunction == \/.right(Str(s))
    }.toProperties("1. id")

    val derived = {
      forAll { (m: Maybe[String @@ GenTags.Ascii]) =>
        val maybe = m.map(Tag.unwrap)
        val json = maybe.cata(jString, jNull)

        val v = json.jdecode[Maybe[Str]].toOption.get
        v == maybe.map(Str(_))
      }.toProperties("2. derived for Maybe")
    }

    Properties.list(id, derived)
  }

  val encodeJson = {
    implicit val ej = Iso.encodeJson[String, Str]

    val id = forAll { (v: String @@ GenTags.Ascii) =>
      val s = Tag.unwrap(v)

      ej.encode(Str.stringIso.to(s)) == jString(s)
    }.toProperties("1. id")

    val derived = forAll { (m: Maybe[String @@ GenTags.Ascii]) =>
      val maybe = m.map(Tag.unwrap)
      val maybeStr = maybe.map { v => Str.stringIso.to(v) }

      maybeStr.jencode == maybe.jencode
    }.toProperties("2. derived for Maybe")

    Properties.list(id, derived)
  }
}
