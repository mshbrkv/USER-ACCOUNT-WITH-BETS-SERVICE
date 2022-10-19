package service.event

import entity.Event

import scala.concurrent.Future

trait EventService {

  def getActiveBetsByEvent: Future[Seq[Event]]


}
