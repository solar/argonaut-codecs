package org.sazabi.argonaut.codecs.util

import java.net.{ InetAddress, InetSocketAddress }

private[codecs] object NetUtil {
  /**
   * Fast IPv4 address to integer.
   * copied from twitter/util.
   */
  def ipToOptionInt(ip: String): Option[Int] = {
    val dot1 = ip.indexOf('.')
    if (dot1 <= 0) {
      return None
    }
    val dot2 = ip.indexOf('.', dot1 + 1)
    if (dot2 == -1) {
      return None
    }
    val dot3 = ip.indexOf('.', dot2 + 1)
    if (dot3 == -1) {
      return None
    }
    val num1 = ipv4DecimalToInt(ip.substring(0, dot1))
    if (num1 < 0) {
      return None
    }
    val num2 = ipv4DecimalToInt(ip.substring(dot1 + 1, dot2))
    if (num2 < 0) {
      return None
    }
    val num3 = ipv4DecimalToInt(ip.substring(dot2 + 1, dot3))
    if (num3 < 0) {
      return None
    }
    val num4 = ipv4DecimalToInt(ip.substring(dot3 + 1))
    if (num4 < 0) {
      return None
    }
    Some((num1 << 24) | (num2 << 16) | (num3 << 8) | num4)
  }

  /**
   * Faster InetAddress.getByName.
   * copied from twitter/finagle.
   */
  def getByName(host: String): InetAddress = {
    ipToOptionInt(host) match {
      case Some(i) =>
        val bytes = Array[Byte](
          ((i & 0xff000000) >> 24).toByte,
          ((i & 0x00ff0000) >> 16).toByte,
          ((i & 0x0000ff00) >>  8).toByte,
          ((i & 0x000000ff)      ).toByte)
        InetAddress.getByAddress(host, bytes)
      case None =>
        InetAddress.getByName(host)
    }
  }

  /**
   * Fast IPv4 decimal to int.
   * copied from twitter/util.
   */
  def ipv4DecimalToInt(s: String): Int = {
    if (s.isEmpty || s.length > 3) {
      return -1
    }
    var i = 0
    var num = 0
    while (i < s.length) {
      val c = s.charAt(i).toInt
      if (c < '0' || c > '9') {
        return -1
      }
      num = (num * 10) + (c - '0')
      i += 1
    }
    if (num >= 0 && num <= 255) {
      num
    } else {
      -1
    }
  }

  type HostPort = (String, Int)

  /**
   * Parses a comma or space-delimited string of hostname and port pairs into
   * scala pair.
   *
   * copied from twitter/finagle.
   */
  def parseHostPorts(hosts: String): Seq[HostPort] =
    hosts split Array(' ', ',') filter (_.nonEmpty) map (_.split(":")) map { hp =>
      require(hp.size == 2, "You must specify host and port")
      (hp(0), hp(1).toInt)
    }

  /**
   * Parses a comma or space-delimited string of hostname and port pairs.
   * copied from twitter/finagle.
   */
  def parseHosts(hosts: String): Seq[InetSocketAddress] = {
    if (hosts == ":*") return Seq(new InetSocketAddress(0))

    (parseHostPorts(hosts) map { case (host, port) =>
      if (host == "")
        new InetSocketAddress(port)
      else
        new InetSocketAddress(host, port)
    }).toList
  }
}
