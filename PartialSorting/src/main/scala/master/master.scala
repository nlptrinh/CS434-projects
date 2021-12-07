package master

import io.grpc.ServerBuilder

import java.net.InetAddress
import scala.annotation.tailrec
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.protos.messages._
import scala.concurrent.duration.Duration
import scala.language.postfixOps

object master {

  def main(args: Array[String]): Unit = {
    val noOfClients = args(0).toInt
    val server = new MasterServer(noOfClients)
    server.start
    println(server.getAddress)
    val clientInfoList = server.getClientInfo
    val noOfBlocks = clientInfoList.foldRight(0)((info, counter) => info.partitions + counter)
    val keyRanges = makeMachineKeyRanges(clientInfoList, noOfBlocks, KeyRangeList(), 0)
    server.sendKeyRanges(keyRanges)
    clientInfoList.foreach(clientInfo => print(clientInfo.ip + " " + "\n"))
    server.server.shutdown
  }

  @tailrec
  def makeMachineKeyRanges(clientInfoList: List[ClientInfo], noOfBlocks: Int, keyRangeList:
                     KeyRangeList, counter:Int):KeyRangeList = {

    @tailrec
    def makeBlockKeyRanges(clientPartitions:Int, blockKeyRanges: List[String]):List[String] = {
      assert(clientPartitions >= 0)
      if (clientPartitions == 0 ) blockKeyRanges
      else{
        val n = counter+clientPartitions
        val div = 95 * n/noOfBlocks
        val char1 = (div + 31).toChar
        val char2 = ((95*(95.0*n/noOfBlocks - div))+31).toChar
        val str = List(char1, char2).mkString
        makeBlockKeyRanges(clientPartitions-1, str::blockKeyRanges)
      }
    }

    if (clientInfoList == Nil) keyRangeList
    else {
      val clientInfo = clientInfoList.head
      val blockKeyRanges = makeBlockKeyRanges(clientInfo.partitions, List())
      val machineKeyRange = MachineKeyRange(clientInfo, blockKeyRanges)
      val newRangeList = keyRangeList.addKeyRanges(machineKeyRange)
      makeMachineKeyRanges(clientInfoList.tail, noOfBlocks, newRangeList, counter + clientInfo.partitions)
    }
  }

  class MasterServer(val noOfClients:Int) {

    val service = new MasterServiceImpl(noOfClients)
    val serverBuilder = ServerBuilder.forPort(0)
    serverBuilder.addService(MasterServiceGrpc.bindService(service, ExecutionContext.global))
    val server = serverBuilder.build

    def start:Unit =
      server.start
      sys.addShutdownHook {
        println("Server shutting down")
        server.shutdown()
      }

    def getClientInfo = service.getClientInfo
    def sendKeyRanges(keyRangeList: KeyRangeList) = service.sendKeyRanges(keyRangeList)

    def blockUntilShutdown = server.awaitTermination

    def getAddress() = InetAddress.getLocalHost + ":" + server.getPort

  }

  class MasterServiceImpl(val noOfClients:Int) extends MasterServiceGrpc.MasterService {

    val keyRangesPromise = Promise[KeyRangeList]
    val clientInfoPromises = List.fill(noOfClients)(Promise[ClientInfo])

    override def introduction(request: ClientInfo): Future[KeyRangeList] = {
      println("introduction")
      successAddressPromise(request, clientInfoPromises)
      keyRangesPromise.future
    }

    def successAddressPromise(clientInfo: ClientInfo, promiseList: List[Promise[ClientInfo]]):Unit =
      if (!promiseList.head.trySuccess(clientInfo)) successAddressPromise(clientInfo, promiseList.tail)


    def getClientInfo = {
      println("getting client info")
      clientInfoPromises.map(promise => Await.result(promise.future, Duration.Inf))
    }

    def sendKeyRanges(keyRangeList: KeyRangeList) = keyRangesPromise success keyRangeList
  }

}
