package service.bet

import db.buckets.bets.BetBucket
import entity.Bet

import scala.concurrent.{ExecutionContext, Future}


class BetServiceImpl(val bucket: BetBucket)(implicit ex: ExecutionContext) extends BetService {

  override def getBetById(id: String): Future[Option[Bet]] = bucket.getById(id)

  override def getBetByUserId(id: String): Future[Seq[Bet]] = bucket.getBetByUserId(id)

  override def getBetByEventId(eventId: String): Future[Seq[Bet]] = bucket.getBetByEventId(eventId)

  override def createBet(data: Bet, userId: String): Future[Bet] = bucket.createBet(data, userId)
}