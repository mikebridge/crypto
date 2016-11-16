import com.bridgecanada.utils.HexConversions._
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import org.apache.commons.codec.binary.Base64

// SEE: https://gist.github.com/alexandru/ac1c01168710786b54b0

object Encryption {

  def padRight(bytes: Array[Byte]): Array[Byte] = {
    var bytesToAdd = 16 - bytes.length % 16
    Array.fill[Byte](bytesToAdd)(bytesToAdd.byteValue) ++ bytes
  }

  def encryptCBC(key: String, message: String, iv: IvParameterSpec): String = {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec(key), iv)
    var result = iv.getIV ++ cipher.doFinal(message.fromAsciiToHexString.asBytes)
    result.asHexString
  }

  def decryptCBC(key: String, cipherText: String): String = {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec(key), getIV(cipherText))
    cipher.doFinal(cipherText.asBytes.drop(16)).asHexString.readable
  }

  def decryptCTR(key: String, cipherText: String): String = {
    val cipher = Cipher.getInstance("AES/CTR/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec(key), getIV(cipherText))
    cipher.doFinal(cipherText.asBytes.drop(16)).asHexString.readable
  }

  def encryptCTR(key: String, message: String, iv: IvParameterSpec): String = {
    val cipher = Cipher.getInstance("AES/CTR/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec(key), iv)
    var result = iv.getIV ++ cipher.doFinal(message.fromAsciiToHexString.asBytes)
    result.asHexString
  }

  def getIV(encryptedValue:String): IvParameterSpec = {
    var iv = encryptedValue.asBytes.take(16)
    //var iv = Array.fill[Byte](16)(1)
    println("GetIV: " + iv.asHexString)
    new IvParameterSpec(iv)
  }

  def secretKeySpec(key: String): SecretKeySpec =
    new SecretKeySpec(key.asBytes, "AES")

}

def testCBC(cbckey:String, cipherText:String): Unit = {
  val decrypted = Encryption.decryptCBC(cbckey, cipherText)
  val iv = Encryption.getIV(cipherText)
  val cipherText2 = Encryption.encryptCBC(cbckey, decrypted, iv)
  println("MESSAGE: " + decrypted)
  println("CIPHERTEXT1:  " + cipherText)
  println("SHOULD EQUAL: " + cipherText2)
}

def testCTR(ctrkey:String, cipherText:String): Unit = {
  val decrypted = Encryption.decryptCTR(ctrkey, cipherText)
  val iv = Encryption.getIV(cipherText)
  val cipherText2 = Encryption.encryptCTR(ctrkey, decrypted, iv)
  println("MESSAGE: " + decrypted)
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

