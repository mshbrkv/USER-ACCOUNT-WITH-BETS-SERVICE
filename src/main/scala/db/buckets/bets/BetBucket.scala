package db.buckets.bets

import com.couchbase.client.scala.{AsyncCluster, AsyncCollection}
import db.buckets.AbstractBucket
import entity.Bet
import service.event.EventService

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

class BetBucket(cluster: AsyncCluster, eventService: EventService)(implicit ex: ExecutionContext) extends AbstractBucket[Bet] {

  final val bucket = cluster.bucket("Bets")
  final val defaultCollection: AsyncCollection = bucket.defaultCollection

  override def getById(id: String): Future[Option[Bet]] = {
    val eventualResult = defaultCollection.get(convertIdToDocId(id), Duration.apply("5s"))
    eventualResult.map(_.contentAs[Bet].toOption)
  }

  override def deleteById(id: String): Future[Unit] = defaultCollection.remove(convertIdToDocId(id)).map(_ => ())

  override protected def convertIdToDocId(id: String): String = {
    s"B:$id"
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

  def getBetByEventId(eventId: String): Future[Seq[Bet]] = {
    val query =
      s"""
         |SELECT b.*
         |FROM `Bets` b
         |WHERE b.eventId='$eventId'""".stripMargin
    cluster.query(query)
      .map(_.rowsAs(Bet.codec).getOrElse(Seq()).toSeq)
  }

  def createBet(data: Bet, userId: String): Future[Bet] = {
    val newData = data.copy(data.id, data.eventId, userId, data.name, data.price)
    for {
      checkActiveEventId <- validationActiveEventIdOfBet(newData)
      checkPrice <- validationBetPriceInGivenRange(newData)
      bet <- if (checkActiveEventId && checkPrice) defaultCollection.insert(entityToId(newData), newData)(Bet.codec).map(_ => newData) else Future.failed(new RuntimeException("Cannot create new bet"))
    } yield bet
  }

  override def entityToId(data: Bet): String = {
    s"B:${data.id}"
  }

  private def validationBetPriceInGivenRange(data: Bet): Future[Boolean] = {
    Future(data.price >= 10 && data.price <= 10000)
  }

  private def validationActiveEventIdOfBet(data: Bet): Future[Boolean] = {
    for {
      event <- eventService.getById(data.eventId)
      isActive = event.exists(_.inPlay)
    } yield isActive
  }
}
