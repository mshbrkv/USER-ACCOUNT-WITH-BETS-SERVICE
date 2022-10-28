package service.event

import entity.Event
import io.circe.parser.parse
import requests.Response

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl(implicit ex: ExecutionContext) extends EventService {

  private val headers = Seq("Content-Type" -> "application/json", "charset" -> "utf-8")

  override def getById(eventId: String): Future[Option[Event]] = {
    val response: Response = requests.get(s"http://localhost:8082/events/$eventId", readTimeout = 600000, connectTimeout = 6000000, headers = headers)
    val result = for {
      responseAsString <- parse(response.text()).toOption
      event = responseAsString.as[Event]
    } yield event.getOrElse(Option.empty.get)
    Future(result)
  }
}
