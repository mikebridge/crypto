package com.bridgecanada.utils

import javax.xml.bind.DatatypeConverter

object HexConversions {

  def toHexString (byteArray: Array[Byte] ) = DatatypeConverter.printHexBinary (byteArray)
  def toByteArray (s: String) = DatatypeConverter.parseHexBinary (s)

  //implicit def asHexString(byteArray : Array[Byte]): String = toHexString(byteArray)

  implicit class HexString(private val str: String) {

    def asBytes: Array[Byte] = toByteArray(str)
    def ^ (str2: String): String = (str.asBytes ^ str2.asBytes).asHexString

  }

  implicit class AsciiString(private val str: String) {

    def asAsciiHexString: String = str.map(_.toByte).toArray.asHexString
    def fromHexStringToInts: Iterator[Int] = str.sliding(2,2).map(Integer.parseInt(_, 16))

    //def asAsciiHexString(spacer: String): String = str.asAsciiHexString.sliding(2,2).map(x => " " + x).mkString
    //def readable: String = str.sliding(2,2).map(b => String.format("%02X", java.lang.Byte.valueOf(b))).mkString(" ")
    def readable:String = str.sliding(2,2)
      .map(b => java.lang.Integer.parseInt(b, 16).toChar)
      .map(x => if (
          x >= 'a' && x <= 'z' ||
          x >= 'A' && x <= 'Z' ||
          x == '.' ||
          x == ',' ||
          x == '?' ||
          x == '!' ||
          x >= '0' && x <= '9' ||
          x == ' ') x
          else '_')
      .mkString

    def compareAgainstBad(str2:String): String = {
      str.fromHexStringToInts.zip(str2.fromHexStringToInts)
        .map {
          case (a:Int, b:Int) if a == b => "X"
          case (a:Int, b:Int) if a != b => "_"
        }.mkString
    }

    def compareAgainst(str2: String) : String = {
      str.fromHexStringToInts.zip(str2.fromHexStringToInts).map {
        case (a: Int, b: Int) if a == b => "1"
        case (a: Int, b: Int) if a != b => "0"
      }.mkString
    }
//    def compareAgainst(str2: String): String = {
//      str.zip(str2).map {
//        case (a:Char, b:Char) if (a == b) => "X"
//        case (a:Char, b:Char) if (a == b) => " "
//      }.mkString
//    }
  }

  implicit class HexBytes(private val byteArray: Array[Byte]) {
    def asHexString: String = toHexString(byteArray)

    // SEE: http://stackoverflow.com/questions/31221462/scala-defining-own-infix-operators
    def ^ (byteArray2: Array[Byte]): Array[Byte] = byteArray.zip(byteArray2).map {
      case (a:Byte, b:Byte) => (a ^ b).byteValue
    }

  }

}