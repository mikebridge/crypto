import com.bridgecanada.utils.HexConversions._
import java.security.MessageDigest
import java.util
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import org.apache.commons.codec.binary.Base64

// SEE: https://gist.github.com/alexandru/ac1c01168710786b54b0

object Encryption {

  def padRight(bytes: Array[Byte]): Array[Byte] = {
    var bytesToAdd = 16 - bytes.length % 16
    Array.fill[Byte](bytesToAdd)(bytesToAdd.byteValue) ++ bytes
  }

  def encrypt(key: String, message: String, iv: IvParameterSpec): String = {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec(key), iv)
    //var result = cipher.doFinal(message.asAsciiHexString.asBytes)
    var result = cipher.doFinal(iv.getIV ++ message.asAsciiHexString.asBytes)
    //println(result)
    //println("ENCRYPTED USING IV " + cipher.getIV.asHexString)
    //result.asHexString
    //cipher.doFinal(padRight(message.asAsciiHexString.asBytes)).asHexString
    result.asHexString
  }

  def decrypt(key: String, cipherText: String): String = {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec(key), getIV(cipherText))
    cipher.doFinal(cipherText.asBytes.drop(16)).asHexString.readable
  }

  def getIV(encryptedValue:String): IvParameterSpec = {
    var iv = encryptedValue.asBytes.take(16)
    println(iv.asHexString)
    new IvParameterSpec(iv)
  }

  def secretKeySpec(key: String): SecretKeySpec =
    new SecretKeySpec(key.asBytes, "AES")

}

def testCBC(cbckey:String, cipherText:String): Unit = {
  val decrypted = Encryption.decrypt(cbckey, cipherText)
  //println("\""+z1+"\"")
  //println("Using iv " + iv)
  val iv = Encryption.getIV(cipherText)
  val cipherText2 = Encryption.encrypt(cbckey, decrypted, iv)
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


//object EncryptionOrig {
//  def encrypt(key: String, value: String): String = {
//    val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
//    cipher.init(Cipher.ENCRYPT_MODE, keyToSpec(key))
//    Base64.encodeBase64String(cipher.doFinal(value.getBytes("UTF-8")))
//  }
//
//  def decrypt(key: String, encryptedValue: String): String = {
//    val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
//    cipher.init(Cipher.DECRYPT_MODE, keyToSpec(key))
//    new String(cipher.doFinal(Base64.decodeBase64(encryptedValue)))
//  }
//
//  def keyToSpec(key: String): SecretKeySpec = {
//    var keyBytes: Array[Byte] = (SALT + key).getBytes("UTF-8")
//    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
//    keyBytes = sha.digest(keyBytes)
//    keyBytes = util.Arrays.copyOf(keyBytes, 16)
//    new SecretKeySpec(keyBytes, "AES")
//  }
//
//  private val SALT: String =
//    "jMhKlOuJnM34G6NHkqo9V010GhLAqOpF0BePojHgh1HgNg8^72k"
//}
