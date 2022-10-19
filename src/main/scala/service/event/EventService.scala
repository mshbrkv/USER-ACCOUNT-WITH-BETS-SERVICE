package service.event

import entity.Event

import scala.concurrent.Future

trait EventService {

  def getActiveEventsFromAllPages: Future[Seq[Event]]

  def getById(eventId: String): Future[Option[Event]]

}
