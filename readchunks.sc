import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.{Paths, StandardOpenOption}
import java.security.MessageDigest

def readBytes = 65536 / 8

def readChunk(channel: FileChannel, buffer: ByteBuffer) = {
  val lengthRead: Int = channel.read(buffer)
  val bytes = buffer.array().take(lengthRead)
  buffer.clear()
  bytes
}

def sha256(bytes: Array[Byte]) = {
  val messageDigest = MessageDigest.getInstance("SHA-256")
  messageDigest.update(bytes)
  messageDigest.digest(bytes)
}


val channel: FileChannel = FileChannel.open(Paths.get("test.in"), StandardOpenOption.READ)

val buffer = ByteBuffer.allocate(readBytes)

val result = Stream
    .continually(readChunk(channel, buffer))
    .takeWhile(_.length > 0)
    .map(sha256)

println("TODO: write " + result.length + " blocks")

channel.close()