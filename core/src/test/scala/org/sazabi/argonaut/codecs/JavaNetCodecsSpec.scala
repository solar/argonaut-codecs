package org.sazabi.argonaut.codecs

import java.net.InetAddress

import argonaut._, Json._
import org.scalatest._
import scalaz._

class JavaNetCodecsSpec extends FlatSpec with Matchers with Inside with JavaNetCodecs {
  val insproutInetAddr = InetAddress.getByName("insprout.com")
  val insproutIpAddr = insproutInetAddr.getHostAddress

  val localInetAddr = InetAddress.getLocalHost()
  val localIpAddr = localInetAddr.getHostAddress

  "InetAddressEncodeJson" should "encode InetAddress to Json" in {
    val ej = InetAddressEncodeJson

    ej.encode(insproutInetAddr) shouldBe jString(insproutIpAddr)
    ej.encode(localInetAddr) shouldBe jString(localIpAddr)
  }

  "InetAddressDecodeJson" should "decode Json to InetAddress" in {
    val dj = InetAddressDecodeJson

    inside(dj.decode(jString(localIpAddr).hcursor).toDisjunction) {
      case \/-(addr) => addr shouldBe localInetAddr
    }

    inside(dj.decode(jString(insproutIpAddr).hcursor).toDisjunction) {
      case \/-(addr) => addr shouldBe insproutInetAddr
    }
  }
}
