import com.bridgecanada.utils.HexConversions._
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object Encryption {

  val blocklength = 16

  def createCipher(key: String, mode: Int): Cipher = {
    val cipher = Cipher.getInstance("AES/ECB/NoPadding")
    cipher.init(mode, secretKeySpec(key))
    cipher
  }

  def padRightCBC(bytes: Array[Byte]): Array[Byte] = {
    var bytesToAdd = blocklength - bytes.length % blocklength
    bytes ++ Array.fill[Byte](bytesToAdd)(bytesToAdd.byteValue)
  }

  def unpadRightCBC(block: Array[Byte]): Array[Byte] =
    block.take(block.length - block.last)

  def getEncryptedBlocks(cipherText: String): List[Array[Byte]] =
    cipherText.asBytes.grouped(blocklength).drop(1).toList

  def getIV(cipherText:String): Array[Byte] =
    cipherText.asBytes.take(blocklength)

  def secretKeySpec(key: String): SecretKeySpec =
    new SecretKeySpec(key.asBytes, "AES")

  // SEE: https://www-origin.coursera.org/learn/crypto/lecture/wlIX8/modes-of-operation-many-time-key-cbc
  // at 01:06
  def encryptCBC(key: String, message: String, iv: Array[Byte]): String = {

    val cipher = createCipher(key, Cipher.ENCRYPT_MODE)

    padRightCBC(message.asAsciiHexString.asBytes)
      .grouped(blocklength)
      .scanLeft(iv)((a, b) => cipher.doFinal(a ^ b))
      .map(_.asHexString)
      .mkString
  }

  def unpadBlocks(messageBlocks: Array[Array[Byte]]) =
    messageBlocks.take(messageBlocks.length - 1) :+ unpadRightCBC(messageBlocks.last)


  def getIVAt(iv: Array[Byte], i: Int): Array[Byte] = {
    val nonce = iv.take(blocklength / 2)
    val counter = BigInt(iv.slice(blocklength/2, blocklength))
    nonce ++ (counter + i).toByteArray
  }

  // SEE: https://www-origin.coursera.org/learn/crypto/lecture/wlIX8/modes-of-operation-many-time-key-cbc
  // at 02:26
  def decryptCBC(key: String, cipherText: String): String = {

    val cipher = createCipher(key, Cipher.DECRYPT_MODE)

    val encryptedBlocks = getEncryptedBlocks(cipherText)

    val messageBlocks = encryptedBlocks.map(cipher.doFinal)
                        .toArray.zip(getIV(cipherText) :: encryptedBlocks)
                        .map {
                          case (a,b) => a ^ b
                        }

    unpadBlocks(messageBlocks).map(_.asHexString).mkString.readable
  }


  def decryptCTR(key: String, cipherText: String): String = {
    val cipher = createCipher(key, Cipher.ENCRYPT_MODE)
    val iv = getIV(cipherText)
    val encryptedBlocks = getEncryptedBlocks(cipherText)

    val hexBlocks = encryptedBlocks.zipWithIndex.map {
      case (encryptedBlock, i) => encryptedBlock ^ cipher.doFinal(getIVAt(iv, i))
    }

    hexBlocks.map(_.asHexString).mkString.readable

  }

  // SEE: https://www-origin.coursera.org/learn/crypto/lecture/5wL84/modes-of-operation-many-time-key-ctr
  // at 1:55
  def encryptCTR(key: String, message: String, iv: Array[Byte]): String = {

    val cipher = createCipher(key, Cipher.ENCRYPT_MODE)

    iv.asHexString + message.asAsciiHexString.asBytes
      .grouped(blocklength)
      .zipWithIndex.map {
        case (block, i) => cipher.doFinal(getIVAt(iv, i)) ^ block
      }.toArray
      .map(_.asHexString)
      .mkString
  }

}

def testCBC(cbckey:String, cipherText:String): Unit = {
  val decrypted = Encryption.decryptCBC(cbckey, cipherText)
  val iv = Encryption.getIV(cipherText)
  val cipherText2 = Encryption.encryptCBC(cbckey, decrypted, iv)
  println("MESSAGE: >" + decrypted + "<")
  println("CIPHERTEXT1:  " + cipherText)
  println("SHOULD EQUAL: " + cipherText2)
}

def testCTR(ctrkey:String, cipherText:String): Unit = {
  val decrypted = Encryption.decryptCTR(ctrkey, cipherText)
  val iv = Encryption.getIV(cipherText)
  val cipherText2 = Encryption.encryptCTR(ctrkey, decrypted, iv)
  println("MESSAGE: >" + decrypted + "<")
  println("CIPHERTEXT1:  " + cipherText)
  println("SHOULD EQUAL: " + cipherText2)
}


val cbckey1 = "140b41b22a29beb4061bda66b6747e14"
val cipherText1 = "4ca00ff4c898d61e1edbf1800618fb2828a226d160dad07883d04e008a7897ee2e4b7465d5290d0c0e6c6822236e1daafb94ffe0c5da05d9476be028ad7c1d81"
testCBC(cbckey1, cipherText1)

val cbckey2 = "140b41b22a29beb4061bda66b6747e14"
val cipherText2 = "5b68629feb8606f9a6667670b75b38a5b4832d0f26e1ab7da33249de7d4afc48e713ac646ace36e872ad5fb8a512428a6e21364b0c374df45503473c5242a253"
testCBC(cbckey2, cipherText2)

val ctrkey3 = "36f18357be4dbd77f050515c73fcf9f2"
val cipherText3 = "69dda8455c7dd4254bf353b773304eec0ec7702330098ce7f7520d1cbbb20fc388d1b0adb5054dbd7370849dbf0b88d393f252e764f1f5f7ad97ef79d59ce29f5f51eeca32eabedd9afa9329"
testCTR(ctrkey3, cipherText3)

val ctrkey4 = "36f18357be4dbd77f050515c73fcf9f2"
val cipherText4 = "770b80259ec33beb2561358a9f2dc617e46218c0a53cbeca695ae45faa8952aa0e311bde9d4e01726d3184c34451"
testCTR(ctrkey4, cipherText4)

