package service.event

import entity.Event
import io.circe.parser.parse
import requests.Response

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl(implicit ex: ExecutionContext) extends EventService {

  override def getActiveBetsByEvent: Future[Seq[Event]] = {


    val headers = Seq(("Content-Type" -> "application/json"), ("charset" -> "utf-8"))

    val response: Response = requests.get("http://localhost:8082/events/in_play=true", readTimeout = 600000, connectTimeout = 6000000, headers = headers)

    val activeEventIds: Seq[Event] = (for {
      responseAsString <- parse(response.text()).toOption
      responseCursor = responseAsString.hcursor
      contentCursor = responseCursor.downField("content")
      contentValues = contentCursor.values.getOrElse(Seq())
      event = contentValues.map(_.as[Event])
    } yield event)
      .getOrElse(Seq())
      .toSeq.flatMap(_.toOption)

    Future(activeEventIds)
  }
}
