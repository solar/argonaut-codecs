package org.sazabi.argonaut.codecs

import argonaut._
import argonaut.DecodeJson._
import argonaut.Json._
import com.google.common.net.{ HostAndPort, InetAddresses }

import java.net.{ InetAddress, InetSocketAddress, UnknownHostException, URL }

import scala.util.{ Failure, Success, Try }

trait JavaNetCodecs {
  private[this] def stringToInetAddr(s: String, a: HCursor): DecodeResult[InetAddress] = {
    Try(InetAddresses.forString(s)).orElse(Try(InetAddress.getByName(s))) match {
      case Success(addr) => DecodeResult.ok(addr)
      case Failure(e: UnknownHostException) =>
        DecodeResult.fail(e.getMessage, a.history)
      case Failure(_) =>
        DecodeResult.fail("Invalid expression for InetAddress", a.history)
    }
  }

  implicit val InetAddressDecodeJson: DecodeJson[InetAddress] =
    DecodeJson(a => a.focus.number orElse a.focus.string match {
      case Some(n: JsonNumber) => {
        n.toInt.map { i =>
          DecodeResult.ok[InetAddress](InetAddresses.fromInteger(i))
        }.getOrElse(DecodeResult.fail("Invalid range for Inet4Address", a.history))
      }
      case Some(s: String) => stringToInetAddr(s, a)
      case _ => DecodeResult.fail("Invalid expression for InetAddress", a.history)
    })

  implicit val InetAddressEncodeJson: EncodeJson[InetAddress] =
    EncodeJson { a =>
      jString(a.getHostAddress)
    }

  implicit val HostAndPortDecodeJson: DecodeJson[HostAndPort] = {
    DecodeJson(a => a.focus.string.map { s =>
      Try(HostAndPort.fromString(s))
    } match {
      case Some(Success(hap)) => DecodeResult.ok(hap)
      case Some(Failure(e)) => DecodeResult.fail(e.getMessage, a.history)
      case None => DecodeResult.fail("invalid expression for HostAndPort", a.history)
    })
  }

  implicit val HostAndPortEncodeJson: EncodeJson[HostAndPort] =
    EncodeJson { hp => jString(hp.toString) }

  implicit def InetSocketAddressEncodeJson: EncodeJson[InetSocketAddress] =
    EncodeJson { a =>
      val host = a.getHostString
      val port = a.getPort
      jString(s"$host:$port")
    }

  implicit val InetSocketAddressDecodeJson: DecodeJson[InetSocketAddress] =
    DecodeJson(a => a.focus.string.map { s =>
      Try(HostAndPort.fromString(s)).flatMap { hp =>
        val host = hp.getHostText
        val port = hp.getPort

        Try {
          if (host.isEmpty) new InetSocketAddress(port)
          else new InetSocketAddress(host, port)
        }
      }
    } match {
      case Some(Success(addr)) => DecodeResult.ok(addr)
      case Some(Failure(e)) => DecodeResult.fail(e.getMessage, a.history)
      case None => DecodeResult.fail("invalid expression for InetSocketAddress",
        a.history)
    })

  implicit val URLDecodeJson: DecodeJson[URL] = DecodeJson.optionDecoder(
    j => j.string.flatMap { s => Try(new URL(s)).toOption },
    "Invalid expression for URL")

  implicit val URLEncodeJson: EncodeJson[URL] = EncodeJson { a =>
    jString(a.toString)
  }
}
