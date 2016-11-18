import com.bridgecanada.utils.HexConversions._

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file._
import java.security.MessageDigest

def readChunkSize = 1024

def reverseFileStream(channel: FileChannel, chunkSize: Int): Stream[Array[Byte]] = {

  val chunkIndices = (0L to channel.size() by chunkSize).reverse
  val buffer = ByteBuffer.allocate(chunkSize)

  chunkIndices.toStream.map(index => {
    buffer.clear()
    val lengthRead: Int = channel.read(buffer, index)
    buffer.array().take(lengthRead)
  })

}


def sha256(bytes: Array[Byte]) : Array[Byte] = {
  val messageDigest = MessageDigest.getInstance("SHA-256")
  messageDigest.update(bytes)
  messageDigest.digest
}

def validateSha256(message: Array[Byte], hash: Array[Byte]) =
  sha256(message) sameElements hash

//val inputFile: Path = Paths.get("/home/bridge/work/crypto/6.2.birthday.mp4_download")

val inputFile: Path = Paths.get("/home/bridge/work/crypto/6.1.intro.mp4_download")
val channel = FileChannel.open(inputFile, StandardOpenOption.READ)

val result = reverseFileStream(channel, readChunkSize)
          .scanLeft(Array[Byte]())((hash, block) => sha256(block ++ hash))

def streamIsValid(hash:Array[Byte], messages: Stream[Array[Byte]]): Boolean = {
  val valid = validateSha256(hash, messages.head)
  val nextHash = messages.head.drop(readChunkSize).tail
  valid && streamIsValid(nextHash, messages.tail)
}

val resultReversed = result.reverse

val h1 = resultReversed.tail.head
val h0: Array[Byte] = resultReversed.head

//if (streamIsValid(h0, resultReversed.tail)) {
//  println("Stream is valid")
//} else {
//  println("Stream is invalid")
//}

// 5b96aece304a1422224f9a41b228416028f9ba26b0d1058f400200f06a589949
// TODO: h1 should be a byte string plus a hash.
println("RESULT:  >" + h1.asHexString+ "<")
println("RESULT:  >" + h0.asHexString+ "<")
channel.close()