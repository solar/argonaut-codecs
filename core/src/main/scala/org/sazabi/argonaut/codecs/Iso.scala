package org.sazabi.argonaut.codecs

import scala.language.higherKinds

import argonaut._
import scalaz.Isomorphism._
import shapeless.Lazy

object Iso {
  def functorDecodeJson[F[_], G[_], A](implicit d: Lazy[DecodeJson[F[A]]], iso: Lazy[F <~> G]): DecodeJson[G[A]] =
    d.value.map(iso.value.to)

  def functorEncodeJson[F[_], G[_], A](implicit e: Lazy[EncodeJson[F[A]]], iso: Lazy[F <~> G]): EncodeJson[G[A]] =
    e.value.contramap(iso.value.from)

  def decodeJson[A, B](implicit d: Lazy[DecodeJson[A]], iso: Lazy[A <=> B]): DecodeJson[B] =
    d.value.map(iso.value.to)

  def encodeJson[A, B](implicit e: Lazy[EncodeJson[A]], iso: Lazy[A <=> B]): EncodeJson[B] =
    e.value.contramap(iso.value.from)
}
