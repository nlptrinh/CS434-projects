package master

import scala.protos.messages._
import java.io._
import java.net._


object master {

  def main(args: Array[String]): Unit = {
    val ss = new ServerSocket(6666)
    val s = ss.accept
    println(s.getInetAddress)
    val dis = new DataInputStream(s.getInputStream)
    val ds = DataSet.parseFrom(dis)
    val outputFile = new File("sortedPartition")
    val printWriter = new PrintWriter(outputFile)
    ds.data.foreach(x => printWriter.write(x.key + " " + x.value + "\n"))
    printWriter.close()
    ss.close
  }

}