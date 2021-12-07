package master

import io.grpc.ServerBuilder

import scala.protos.messages._
import java.io._
import java.net._
import scala.concurrent.{ExecutionContext, Future, Promise}



object master {

  private class GreeterImpl extends GreeterGrpc.Greeter {
    val p = Promise[DataReply]
    val list = List.fill(2) (Promise[Int])
    val listF = list.map(_.future)


    override def sendData(request: DataRequest): Future[DataReply] = {
      val ds = request.dataSet
      val outputFile = new File("sortedPartition")
      val printWriter = new PrintWriter(outputFile)
      //ds.data.foreach(x => printWriter.write(x.key + " " + x.value + "\n"))
      //printWriter.close()
      println("Sending")
      val reply = DataReply("Recieved")
      p.future
    }

    def addData(dataSet: DataSet, list:List[Promise[Int]]):Unit = {
      if (!list.head.trySuccess(1)) addData(dataSet, list.tail)
    }


    def sendReply() =
      p success new DataReply("send")
  }

  def main(args: Array[String]): Unit = {
    val serverBuilder = ServerBuilder.forPort(4444)
    val greeter = new GreeterImpl
    serverBuilder.addService(GreeterGrpc.bindService(greeter, ExecutionContext.global))
    val server = serverBuilder.build
    server.start
    greeter.sendReply
    server.awaitTermination
  }

}