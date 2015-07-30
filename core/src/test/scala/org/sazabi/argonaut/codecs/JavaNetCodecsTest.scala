package org.sazabi.argonaut.codecs

import java.net.{ InetAddress, InetSocketAddress, URL }

import argonaut._, Json._, JsonIdentity._
import com.google.common.net.{ HostAndPort, InetAddresses }
import scalaprops._, Property.forAll
import scalaz.std.string._

object JavaNetCodecsTest extends Scalaprops with JavaNetCodecs {
  private[this] implicit val inetAddr = Gen.genIntAll.map[InetAddress] { i =>
    InetAddresses.fromInteger(i)
  }

  private[this] val validHost = Gen.elements(
    "",
    "www.google.jp",
    "www.google.com",
    "www.amazon.jp",
    "127.0.0.1",
    "192.168.11.153")

  private[this] val invalidHost = Gen.elements(
    "unknown",
    "184.234.10984.2",
    "---",
    "ho ge")

  private[this] val validPort = Gen.choose(0, 65535)

  private[this] val invalidPort = Gen.oneOf(Gen.choose(Int.MinValue, -1),
    Gen.choose(65536, Int.MaxValue))

  val inetAddressEncodeJson = {
    forAll { (addr: InetAddress) =>
      addr.jencode == jString(addr.getHostAddress)
    }
  }

  val inetAddressDecodeJson = {
    val string = forAll { (addr: InetAddress) =>
      val json = jString(addr.getHostAddress)
      json.jdecode[InetAddress].toOption == Some(addr)
    }.toProperties("1. valid ipv4 string literal")

    val int = forAll { (i: Int) =>
      val addr = InetAddresses.fromInteger(i)
      jNumber(i).jdecode[InetAddress].toOption == Some(addr)
    }.toProperties("2. valid ipv4 integer literal")

    val valid = forAll { (s: String) =>
      jString(s).jdecode[InetAddress].toOption.isDefined
    }(validHost).toProperties("3. valid host name")

    val invalid = forAll { (s: String) =>
      jString(s).jdecode[InetAddress].toOption.isEmpty
    }(invalidHost).toProperties("4. invalid host name")

    val outOfRange = forAll {
      jNumber(Long.MaxValue).jdecode[InetAddress].toOption.isEmpty
    }.toProperties("5. number out of range")

    Properties.list(string, int, valid, invalid, outOfRange)
  }

  val hostAndPortEncodeJson = {
    forAll { (s: String, i: Int) =>
      val hp = HostAndPort.fromParts(s, i)
      hp.jencode == jString(s"$s:$i")
    }(validHost, validPort).toProperties("valid host")
  }

  val hostAndPortDecodeJson = {
    val valid = forAll { (s: String, i: Int) =>
      val js = jString(s"$s:$i")

      js.jdecode[HostAndPort].toOption == Some(HostAndPort.fromParts(s, i))
    }(validHost, validPort).toProperties("valid host")

    val invalid = forAll { (s: String, i: Int) =>
      val js = jString(s"$s:$i")
      js.jdecode[HostAndPort].toOption.isEmpty
    }(validHost, invalidPort).toProperties("invalid port")

    Properties.list(valid, invalid)
  }

  val inetSocketAddressEncodeJson = {
    val p1 = forAll { (s: String, i: Int) =>
      if (s.isEmpty) {
        new InetSocketAddress(i).jencode == jString(s"0.0.0.0:$i")
      } else {
        new InetSocketAddress(s, i).jencode == jString(s"$s:$i")
      }
    }(validHost, validPort).toProperties("valid")

    val p2 = forAll { (s: String, i: Int) =>
      if (s.isEmpty) {
        new InetSocketAddress(i).jencode == jString(s"0.0.0.0:$i")
      } else {
        new InetSocketAddress(s, i).jencode == jString(s"$s:$i")
      }
    }(invalidHost, validPort).toProperties("invalid host")

    val p3 = forAll { (s: String, i: Int) =>
      try {
        new InetSocketAddress(s, i)
        false
      } catch { case _: Throwable => true }
    }(validHost, invalidPort).toProperties("invalid port")

    Properties.list(p1, p2, p3)
  }

  val inetSocketAddressDecodeJson = {
    val p1 = forAll { (s: String, i: Int) =>
      val json = jString(s"$s:$i")
      val addr = {
        if (s.isEmpty) new InetSocketAddress(i)
        else new InetSocketAddress(s, i)
      }

      json.jdecode[InetSocketAddress].toOption == Some(addr)
    }(validHost, validPort).toProperties("valid")

    val p2 = forAll { (s: String, i: Int) =>
      val json = jString(s"$s:$i")
      val addr = {
        if (s.isEmpty) new InetSocketAddress(i)
        else new InetSocketAddress(s, i)
      }

      json.jdecode[InetSocketAddress].toOption == Some(addr)
    }(invalidHost, validPort).toProperties("invalid host")

    val p3 = forAll { (s: String, i: Int) =>
      val json = jString(s"$s:$i")
      json.jdecode[InetSocketAddress].toOption.isEmpty
    }(validHost, invalidPort).toProperties("invalid port")

    Properties.list(p1, p2, p3)
  }

  // @TODO need more test
  val urlEncodeJson = Property.prop {
    new URL("http://google.jp").jencode == jString("http://google.jp")
  }

  val urlDecodeJson = {
    val p1 = Property.prop {
      jString("http://google.jp").jdecode[URL].toOption ==
        Some(new URL("http://google.jp"))
    }.toProperties("valid url")

    val p2 = Property.prop {
      jString("gggg://google.jp").jdecode[URL].toOption.isEmpty
    }.toProperties("invalid scheme")

    Properties.list(p1, p2)
  }
}
