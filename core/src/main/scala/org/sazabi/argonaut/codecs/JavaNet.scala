package org.sazabi.argonaut.codecs

import util.NetUtil

import argonaut._
import argonaut.Json._
import argonaut.DecodeJson._

import java.net.{ InetAddress, InetSocketAddress, UnknownHostException, URL }

import scala.util.{ Failure, Success, Try }

trait JavaNetCodecs {
  private[this] def toInetAddress(opt: Option[Int], a: HCursor): DecodeResult[InetAddress] = {
    opt.map { i =>
      val bytes = Array[Byte](
        ((i & 0xff000000) >> 24).toByte,
        ((i & 0x00ff0000) >> 16).toByte,
        ((i & 0x0000ff00) >>  8).toByte,
        ((i & 0x000000ff)).toByte)

      Try(InetAddress.getByAddress(bytes))
    }.getOrElse(Failure(new IllegalArgumentException())) match {
      case Success(addr) => DecodeResult.ok(addr)
      case Failure(e: UnknownHostException) =>
        DecodeResult.fail(e.getMessage, a.history)
      case _ =>
        DecodeResult.fail("Invalid expression for InetAddress", a.history)
    }
  }

  private[this] def stringToInetAddress(s: String, a: HCursor):
      DecodeResult[InetAddress] = {
    Try(NetUtil.getByName(s)) match {
      case Success(addr) => DecodeResult.ok(addr)
      case Failure(e: UnknownHostException) =>
        DecodeResult.fail(e.getMessage, a.history)
      case Failure(_) =>
        DecodeResult.fail("Invalid expression for InetAddress", a.history)
    }
  }

  implicit def InetAddressDecodeJson: DecodeJson[InetAddress] =
    DecodeJson(a => a.focus.number orElse a.focus.string match {
      case Some(n: JsonNumber) => toInetAddress(n.toInt, a)
      case Some(s: String) => stringToInetAddress(s, a)
      case _ => DecodeResult.fail("Invalid expression for InetAddress", a.history)
    })

  implicit def InetAddressEncodeJson: EncodeJson[InetAddress] =
    EncodeJson { a =>
      jString(a.getHostAddress)
    }

  implicit def InetSocketAddressDecodeJson: DecodeJson[InetSocketAddress] =
    DecodeJson(a => a.focus.string.flatMap { host =>
      Try(NetUtil.parseHosts(host).head).toOption
    } match {
      case Some(addr) => DecodeResult.ok(addr)
      case _ =>
        DecodeResult.fail("Invalid expression for InetSocketAddress", a.history)
    })

  implicit def InetSocketAddressEncodeJson: EncodeJson[InetSocketAddress] =
    EncodeJson { a =>
      val str = a.toString
      jString(str.drop(str.indexOf('/') + 1))
    }

  implicit val URLDecodeJson: DecodeJson[URL] = DecodeJson.optionDecoder(
    j => j.string.flatMap { s => Try(new URL(s)).toOption },
    "Invalid expression for URL")

  implicit val URLEncodeJson: EncodeJson[URL] = EncodeJson { a =>
    jString(a.toString)
  }
}
