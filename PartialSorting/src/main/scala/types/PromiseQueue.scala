package types

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration.Duration
import scala.protos.messages.ClientInfo

class PromiseQueue[T](size:Int){
  val queue = List.fill(size)(Promise[T]())

  def success(succeedElem: T):Unit = {
    def successRec(queueRec: List[Promise[T]]):Unit= {
      assert(queueRec != Nil)
      if (!queueRec.head.trySuccess(succeedElem)) successRec(queueRec.tail)
    }
    successRec(queue)
  }
  def waitForQueue = queue.map(promise => Await.result(promise.future, Duration.Inf))

}
