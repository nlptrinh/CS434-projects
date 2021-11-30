package worker

import scala.protos.messages._
import java.io._
import java.net._
import scala.io.Source

object worker {
  def stringToData(str:String):Data = {
    val dataWords = str.split(" ", 2)
    Data(key = dataWords(0), value = dataWords(1))
  }

  def dataLessThan(d1:Data, d2:Data) = {
    if (d1.key < d2.key) true else false
  }

  def main(args: Array[String]): Unit = {
    val filename = "partition1"
    val lines = Source.fromFile(filename).getLines.toList
    val sortedData = lines.map(stringToData).sortWith(dataLessThan)
    val dataset = DataSet().addAllData(sortedData)
    println(dataset.toProtoString)

    val s = new Socket("localhost", 6666)
    println(s.getInetAddress)
    println(s.getPort)
    val dos = new DataOutputStream(s.getOutputStream)
    dataset.writeTo(dos)
    dos.flush
    dos.close
    s.close

  }

}
