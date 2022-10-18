package service.bet

import db.buckets.bets.BetBucket
import entity.{Bet, Event}
import io.circe.Json
import io.circe.parser._
import requests.Response

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}


class BetServiceImpl(val bucket: BetBucket)(implicit ex: ExecutionContext) extends BetService {

  override def getBetById(id: String): Future[Option[Bet]] = bucket.getById(id)

  override def getBetByUserId(id: String): Future[Seq[Bet]] = bucket.getBetByUserId(id)

  override def getBetByEventIdOneUser(id: UUID, userId: String): Future[Seq[Bet]] = bucket.getBetByEventIdOneUser(id, userId)

  override def getActiveBetsByEvent: Future[Seq[Event]] = {

    val headers = Seq(("Content-Type" -> "application/json"), ("charset" -> "utf-8"))

    val response: Response = requests.get("http://localhost:8082/events/in_play=true", readTimeout = 600000, connectTimeout = 6000000, headers = headers)


//    val a = (for {
//      responseAsString <- parse(response.text()).toOption
//      responseCursor = responseAsString.hcursor
//      contentCursor = responseCursor.downField("content")
//    } yield contentCursor.values.getOrElse(Seq()))
//      .getOrElse(Seq())


    val activeEventIds: Seq[Event] = (for {
      responseAsString <- parse(response.text()).toOption
      responseCursor = responseAsString.hcursor
      contentCursor = responseCursor.downField("content").downField("id")
      contentValues = contentCursor.values.getOrElse(Seq())
      event = contentValues.map(_.as[Event])
    } yield event)
      .getOrElse(Seq())
      .toSeq.flatMap(_.toOption)
//      .map(_.id)


    activeEventIds

    Future(activeEventIds)
  }
}
