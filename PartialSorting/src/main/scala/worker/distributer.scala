package worker
import scala.protos.messages._
import io.grpc._
import scala.io.Source

object distributer {

    def stringToData(str:String):Data = {
      val dataWords = str.split(" ", 2)
      Data(key = dataWords(0), value = dataWords(1))
    }

    def dataLessThan(d1:Data, d2:Data) = {
      if (d1.key < d2.key) true else false
    }

    def main(args: Array[String]): Unit = {
      val partFile1 = "partition.1"
      val partFile2 = "partition.2"
      val lines1 = Source.fromFile(partFile1).getLines.toList
      val lines2 = Source.fromFile(partFile2).getLines.toList

      val sortedData1 = lines1.map(stringToData).sortWith(dataLessThan)
      val sortedData2 = lines2.map(stringToData).sortWith(dataLessThan)

      val median1 = sortedData1(sortedData1.size/2).key
      val median2 = sortedData2(sortedData2.size/2).key

      println(median1)
      println(median2)

    }
}
