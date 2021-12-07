package worker

import io.grpc.{ManagedChannelBuilder, ServerBuilder}
import javafx.concurrent.Worker
import master.master.MasterServiceImpl

import java.io.File
import java.net.InetAddress
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source
import scala.protos.messages._


object worker {

  type PromiseQueue[T] = List[Promise[T]]

  def main(args: Array[String]): Unit = {
    val argsList = args.toList
    val serverAddress = getServerAddress(argsList)
    val inputDirs = getDirectoriesFromArgs(argsList.tail, "-I")
    val outputDir = getDirectoriesFromArgs(argsList.tail, "-O").last
    val inputBlocks = blocksFromDirs(inputDirs)
    val totalBlocksCount = inputBlocks.foldRight(0)((files, counter) => files.size + counter)

    val workerServer = makeWorkerServer()
    workerServer.start

    val masterBlockingStub = getMasterBlockingStub(serverAddress)
    val clientInfo = ClientInfo(outputDir, workerServer.getPort, totalBlocksCount)
    val keyRanges = masterBlockingStub.introduction(clientInfo)


    inputBlocks.flatten.foreach(file => partitionAndSample(file, keyRanges))

  }

  def getSortedDataFromFile(file: File) = {
    val lines = Source.fromFile(file).getLines.toList
    lines.map(stringToData).sortWith(dataLessThan)
  }

  def stringToData(str:String):Data = {
    val dataWords = str.split(" ", 2)
    Data(key = dataWords(0), value = dataWords(1))
  }

  def dataLessThan(d1:Data, d2:Data) = if (d1.key < d2.key) true else false


  def partition(sortedData: List[Data], keyRangeList: List[MachineKeyRange], result:List[(List[DataSet], ClientInfo)]):
                List[(List[DataSet], ClientInfo)] = {

    def partitionForClient(data: List[Data], blockKeyRanges: List[String], result:List[DataSet]):
                (List[DataSet], List[Data]) = {
      if(blockKeyRanges == Nil) (result, data)
      else{
        val partitionedData = data.partition(line => line.key <= blockKeyRanges.head)
        val dataSet = DataSet(partitionedData._1)
        partitionForClient(partitionedData._2, blockKeyRanges.tail, result ::: List(dataSet))
      }
    }

    if (keyRangeList == Nil) result
    else{
      val partitionsAndDroppedData = partitionForClient(sortedData, keyRangeList.head.blockKeyRanges.toList, List())
      val partitionsAndClientInfo = (partitionsAndDroppedData._1, keyRangeList.head.clientInfo)
      partition(partitionsAndDroppedData._2, keyRangeList.tail, partitionsAndClientInfo::result)
    }
  }


  def partitionAndSample(file: File, keyRangeList: KeyRangeList) = Future {
    val sortedData = getSortedDataFromFile(file)
    val dataPacks = partition(sortedData, keyRangeList.keyRanges.toList, List())
    dataPacks.foreach(sendPackage(_))

  }

  def sendPackage(packAndClientInfo: (List[DataSet], ClientInfo)) = {
    val dataPackage = DataPackage(packAndClientInfo._1)
    val stub = getWorkerStub(packAndClientInfo._2)
    stub.sendData(dataPackage)
  }

  def failWithMessage(message:String): Unit ={
    println(message)
    System.exit(2)
  }

  def getServerAddress(args: List[String]) = {
    try {
      val splitStringAtColon = args.head.split (":")
      require (splitStringAtColon.size == 2)
      val port = splitStringAtColon (1).toInt
      (splitStringAtColon(0), port)
    } catch {
      case e: Exception => failWithMessage("First argument must be a valid ip address")
        ("fail", -1)
    }
  }

  def getDirectoriesFromArgs(argsList: List[String], argName:String):List[String] = {
    if(argsList == Nil) {
      failWithMessage("Missing argument '" + argName + "'+ folders")
    }
    def createDirList(args: List[String], folders:List[String]):List[String] = {
      if (args == Nil || args.head.charAt(0) == '-') folders
      else createDirList(args.tail, args.head :: folders)
    }
    if(argsList.head != argName) getDirectoriesFromArgs(argsList.tail, argName)
    else createDirList(argsList.tail, List())
  }

  def blocksFromDirs(dirList: List[String]) = {

    def filesFromDir(dir:String) = {
      val d = new File(dir)
      if(d.exists && d.isDirectory) {
        d.listFiles.filter(_.isFile).toList
      } else {
        failWithMessage(dir + " is not a directory")
        List()
      }
    }
    dirList.map(filesFromDir(_))
  }

  def makeWorkerServer(promiseLists: List[List[Promise[DataSet]]]) = {
    val service = new WorkerServiceImpl(promiseLists)
    val serverBuilder = ServerBuilder.forPort(0)
    serverBuilder.addService(WorkerServiceGrpc.bindService(service, ExecutionContext.global))
    serverBuilder.build
  }

  def getMasterBlockingStub(serverAddress: (String, Int)) = {
    val channelBuilder = ManagedChannelBuilder.forAddress(serverAddress._1, serverAddress._2)
    channelBuilder.usePlaintext
    val channel = channelBuilder.build
    MasterServiceGrpc.blockingStub(channel)
  }

  def getWorkerStub(clientInfo: ClientInfo)= {
    val channelBuilder = ManagedChannelBuilder.forAddress(clientInfo.ip, clientInfo.port)
    channelBuilder.usePlaintext
    val channel = channelBuilder.build
    WorkerServiceGrpc.stub(channel)
  }

  def successPromiseList(promiseList:List[Promise[DataSet]], dataSet:DataSet):Unit = {
        assert(promiseList != Nil)
        if (!promiseList.head.trySuccess(dataSet)) successPromiseList( promiseList.tail, dataSet)
      }

  class WorkerServiceImpl(promiseLists: List[List[Promise[DataSet]]]) extends  WorkerServiceGrpc.WorkerService {
    override def sendData(request: DataPackage): Future[DataReply] = {
      assert(request.dataSets.size == promiseLists.size)
      giveDataToThreads(promiseLists,request.dataSets.toList)
      Future.successful(DataReply("Received"))
    }

    def giveDataToThreads(promiseLists: List[List[Promise[DataSet]]], dataSets: List[DataSet]):Unit = {
      if(promiseLists != Nil) {
        successPromiseList(promiseLists.head, dataSets.head)
        giveDataToThreads(promiseLists.tail, dataSets.tail)
      }
    }
  }



}
