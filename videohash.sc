// HexConversions can be found at
// https://gist.github.com/mikebridge/5ca50d3f98ae2c5eeadd4bc8b6c15018

import com.bridgecanada.utils.HexConversions._
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file._
import java.security.MessageDigest

import scala.annotation.tailrec

def readChunkSize = 1024

def fileName = "/home/bridge/work/crypto/6.1.intro.mp4_download"

val inputFile: Path = Paths.get(fileName)

val channel = FileChannel.open(inputFile, StandardOpenOption.READ)

object VideoEncoder {

  def openReverseFileStream(channel: FileChannel, chunkSize: Int): Stream[Array[Byte]] = {

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


  @tailrec def streamIsValid(hash:Array[Byte], messagesWithHash: Stream[Array[Byte]]): Boolean = {

    if (messagesWithHash.isEmpty)
      return true

    val messageWithHash = messagesWithHash.head

    if (!validateSha256(messageWithHash, hash))
      return false

    streamIsValid(messageWithHash.drop(messageWithHash.length - hash.length),
      messagesWithHash.tail)
  }

  //val hashes: Stream[Array[Byte]] =
  def hashStream(reverseMessageStream: Stream[Array[Byte]]) = {
    reverseMessageStream
      .scanLeft(Array[Byte]())((hash, block) => sha256(block ++ hash))
      .reverse
  }

}

// turns out STream is memoized. :(

val reverseMessages = VideoEncoder.openReverseFileStream(channel, readChunkSize)

val hashes = VideoEncoder.hashStream(reverseMessages)

// after reversing, h0 will be the at the first position
val messages: Stream[Array[Byte]] = reverseMessages.reverse

// join the hashes with the original messages
val result: Stream[Array[Byte]] = Stream(hashes.head) append
     messages.zip(hashes.tail append Stream(Array[Byte]()))
    .map { case (a: Array[Byte], b: Array[Byte]) => a ++ b }

// validate the in-memory stream
if (VideoEncoder.streamIsValid(result.head, result.tail)) {
  println("Stream is valid")
} else {
  println("Stream is invalid")
}

//println("EXPECTED:>03c08f4ee0b576fe319338139c045c89c3e8e9409633bea29442e21425006ea8")
println("EXPECTED:>5b96aece304a1422224f9a41b228416028f9ba26b0d1058f400200f06a589949")
println("RESULT:  >" + result.head.asHexString+ "<")
channel.close()