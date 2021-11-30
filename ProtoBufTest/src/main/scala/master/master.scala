package master

import io.grpc.ServerBuilder

import scala.protos.messages._
import java.io._
import java.net._
import scala.concurrent.{ExecutionContext, Future}



object master {

  private class GreeterImpl extends GreeterGrpc.Greeter {
    override def sendData(request: DataRequest): Future[DataReply] = {
      val ds = request.dataSet
      val outputFile = new File("sortedPartition")
      val printWriter = new PrintWriter(outputFile)
      ds.data.foreach(x => printWriter.write(x.key + " " + x.value + "\n"))
      printWriter.close()
      val reply = DataReply("Recieved")
      Future.successful(reply)
    }
  }

  def main(args: Array[String]): Unit = {
    val serverBuilder = ServerBuilder.forPort(4444)
    serverBuilder.addService(GreeterGrpc.bindService(new GreeterImpl, ExecutionContext.global))
    val server = serverBuilder.build
    server.start
    server.awaitTermination
  }

}