package db.buckets.bets

import com.couchbase.client.scala.kv.GetResult
import com.couchbase.client.scala.{AsyncCluster, AsyncCollection}
import db.buckets.AbstractBucket
import entity.Bet
import service.event.EventService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


class BetBucket(cluster: AsyncCluster, eventService: EventService)(implicit ex: ExecutionContext) extends AbstractBucket[Bet] {

  final val bucket = cluster.bucket("Bets")
  final val defaultCollection: AsyncCollection = bucket.defaultCollection

  override def getById(id: String): Future[Option[Bet]] = {
    defaultCollection.get(convertIdToDocId(id)).map(asObject)
  }

  override def asObject(doc: GetResult): Option[Bet] = {
    doc.contentAs[io.circe.Json] match {
      case Success(value) => value.as[Bet]
      case Failure(exception) => throw exception
    }
  }.toOption

  override def deleteById(id: String): Future[Unit] = defaultCollection.remove(convertIdToDocId(id)).map(_ => ())

  override protected def convertIdToDocId(id: String): String = {
    s"B:${id}"
  }

  def getBetByUserId(id: String): Future[Seq[Bet]] = {

    val query =
      s"""
         |SELECT b.*
         |FROM `Bets` b
         |WHERE b.userId='$id'""".stripMargin

    cluster.query(query)
      .map(_.rowsAs(Bet.codec).getOrElse(Seq()).toSeq)
  }

  def getBetByEventId( eventId: String): Future[Seq[Bet]] = {
    val query =
      s"""
         |SELECT b.*
         |FROM `Bets` b
         |WHERE b.eventId='$eventId'""".stripMargin

    cluster.query(query)
      .map(_.rowsAs(Bet.codec).getOrElse(Seq()).toSeq)
  }

  def getActiveBets: Future[Seq[Bet]] = {
    for {
      activeEvents <- eventService.getActiveEventsFromAllPages
      idActiveEvents = activeEvents.map(_.id.toString).mkString("'", "','", "'")
      query =
        s"""
           |SELECT b.*
           |FROM `Bets` b
           |WHERE b.eventId IN [$idActiveEvents]""".stripMargin
      result <- cluster.query(query).map(_.rowsAs(Bet.codec).getOrElse(Seq()).toSeq)
    } yield result
  }

  def createBet(data: Bet, userId: String): Future[Bet] = {

    val newData = data.copy(data.id, data.eventId, userId, data.name, data.price)

    for {
      checkActiveEventId <- validationActiveEventIdOfBet(newData)
      checkPrice <- validationBetPriceInGivenRange(newData)
      bet <- if (checkActiveEventId && checkPrice) defaultCollection.insert(entityToId(newData), newData)(Bet.codec).map(_ => newData) else Future.failed(new RuntimeException(""))
    } yield bet
  }

  override def entityToId(data: Bet): String = {
    s"B:${data.id}"
  }

  private def validationBetPriceInGivenRange(data: Bet): Future[Boolean] = {
    if (data.price >= 10 && data.price <= 10000) {
      Future(true)
    } else {
      Future(false)
    }
  }

  private def validationActiveEventIdOfBet(data: Bet): Future[Boolean] = {
    for {
      event <- eventService.getById(data.eventId)
      isActive = if (event.get.inPlay.equals(true)) {
        true
      } else {
        false
      }
    } yield isActive
  }
}
