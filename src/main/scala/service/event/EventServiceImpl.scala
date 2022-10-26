package service.event

import entity.Event
import io.circe.parser.parse
import requests.Response

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl()(implicit ex: ExecutionContext) extends EventService {

  private val headers = Seq(("Content-Type" -> "application/json"), ("charset" -> "utf-8"))

  override def getById(eventId: String): Future[Option[Event]] = {
    val response: Response = requests.get(s"http://localhost:8082/events/${eventId}", readTimeout = 600000, connectTimeout = 6000000, headers = headers)
    val result = for {
      responseAsString <- parse(response.text()).toOption
      event = responseAsString.as[Event]
    } yield event.getOrElse(Option.empty.get)
    requests.get
    Future(result)
  }

  override def getActiveEventsFromAllPages: Future[Seq[Event]] = {

    val lastPage = getLastPage
    val seqOfPages = Seq.range(0, lastPage, 1)

    val response: Seq[Response] = seqOfPages.map(page => requests.get(s"http://localhost:8082/events/in_play=true?page=$page", readTimeout = 600000, connectTimeout = 6000000, headers = headers))

    Future(response.flatMap(it => getActiveEventsFromPage(it)))
  }

  private def getActiveEventsFromPage(response: Response): Seq[Event] = {
    (for {
      responseAsString <- parse(response.text()).toOption
      responseCursor = responseAsString.hcursor
      contentCursor = responseCursor.downField("content")
      contentValues = contentCursor.values.getOrElse(Seq())
      event = contentValues.map(_.as[Event])
    } yield event)
      .getOrElse(Seq())
      .toSeq.flatMap(_.toOption)
  }

  private def getLastPage: Int = {

    val response: Response = requests.get("http://localhost:8082/events/in_play=true", readTimeout = 600000, connectTimeout = 6000000, headers = headers)
    val lastPage: Option[Int] = {
      for {
        responseAsString <- parse(response.text()).toOption
        responseCursor = responseAsString.hcursor
        totalPages = responseCursor.downField("totalPages").as[Int].getOrElse(0)
      } yield totalPages
    }
    lastPage.get
  }
}
