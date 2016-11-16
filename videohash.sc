import com.bridgecanada.utils.HexConversions._
import java.io.BufferedInputStream
import java.nio.channels.FileChannel
import java.nio.file.{Files, OpenOption, Paths, StandardOpenOption}
import java.security.MessageDigest


def header(file: String): Array[Byte] = {
  var path= Paths.get(file)
  val size = Files.size(path)
  val chunkSizeInBytes = 1024

  val fc = FileChannel.open(path, StandardOpenOption.READ)
  fc.position(size - 1)
  fc.
  // SEE: https://docs.oracle.com/javase/tutorial/essential/io/rafs.html


//  val bufferedInputStream = new BufferedInputStream(
//    Files.newInputStream())

  val bytes: Array[Byte] = Stream.continually(bufferedInputStream.read)
                            .take(chunkSizeInBytes).toArray.map(x => x.toByte)

  bufferedInputStream.close()
  bytes
}

//val headerBytes = header("/home/bridge/work/crypto/6.1.intro.mp4_download")
val headerBytes = header("/home/bridge/work/crypto/6.2.birthday.mp4_download")
val messageDigest = MessageDigest.getInstance("SHA-256");
messageDigest.update(headerBytes)
val result = messageDigest.digest();
println("EXPECTED: 03c08f4ee0b576fe319338139c045c89c3e8e9409633bea29442e21425006ea8")
println("ACTUAL:   " + result.asHexString)
