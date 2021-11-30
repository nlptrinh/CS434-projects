package worker

import scala.protos.messages._
import io.grpc._
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

    val channelBuilder = ManagedChannelBuilder.forAddress("localhost", 4444)
    channelBuilder.usePlaintext
    val channel = channelBuilder.build
    val request = DataRequest(dataset)
    val blockingStub = GreeterGrpc.blockingStub(channel)
    val reply:DataReply = blockingStub.sendData(request)
    println(reply.reply)
  }

}
