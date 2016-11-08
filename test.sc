//import com.bridgecanada.utils.HexConversions._
//
//var enc1 = "315c4eeaa8b5f8aaf9174145bf43e1784b8fa00dc71d885a804e5ee9fa40b16349c146fb778cdf2d3aff021dfff5b403b510d0d0455468aeb98622b137dae857553ccd8883a7bc37520e06e515d22c954eba5025b8cc57ee59418ce7dc6bc41556bdb36bbca3e8774301fbcaa3b83b220809560987815f65286764703de0f3d524400a19b159610b11ef3e"
//var enc2 = "234c02ecbbfbafa3ed18510abd11fa724fcda2018a1a8342cf064bbde548b12b07df44ba7191d9606ef4081ffde5ad46a5069d9f7f543bedb9c861bf29c7e205132eda9382b0bc2c5c4b45f919cf3a9f1cb74151f6d551f4480c82b2cb24cc5b028aa76eb7b4ab24171ab3cdadb8356f"
//
//val testMessage1 = new StringBuilder(" " * 100).toString.asAsciiHexString
//val testMessage2 = new StringBuilder(" " * 100).toString.asAsciiHexString
//var testMessageXor = testMessage1 ^ testMessage2
//
//var enc1xorenc2 = enc1 ^ enc2
//
//var matches = testMessageXor.findMatchesWith(enc1xorenc2).toArray
//// 30, 53, 56, 70
//var enc1AsInts = enc1.fromHexStringToInts.toArray
//
//def getTestKey = {
//  val idx = 30
//  val testMsgChar: Int = testMessage1.charAt(idx).toInt
//  val enc1Char: Int = enc1AsInts(idx)
//  val keyCharGuess: Int = enc1Char ^ testMsgChar
//  val testKeyTmp: Array[Int] = Array.fill(255) {0}
//  testKeyTmp.update(idx, keyCharGuess)
//  testKeyTmp.map("%02x".format(_)).mkString(" ")
//}
//
////testKeyTmp = testKeyTmp.copy()
////testKeyTmp.update(idx, keyCharGuess)
////testKeyTmp(30) = keyCharGuess
//
//(enc1 ^ getTestKey.asAsciiHexString).readable
//(enc2 ^ getTestKey.asAsciiHexString).readable
//println(getTestKey)
//
////matches.foreach(println)

var a = List(2,4,6)
var b = List(1,2,3,5)
var c = List(1,3,5)
var maxlength = 4
var combined = for (i <- 0 to 3) yield {
  List(a,b,c).map(x => if (x.length > i) Some(x(i)) else None)}

combined.filter(x => x.forall(x => x.isEmpty || x.get > 1))