package service.bet

import db.buckets.bets.BetBucket
import entity.Bet

import scala.concurrent.Future

class BetServiceImpl(val bucket: BetBucket) extends BetService {

  override def getBetById(id: String): Future[Option[Bet]] = bucket.getById(id)

  override def getBetByUserId(id: String): Future[Seq[Bet]] = bucket.getBetByUserId(id)

  override def getBetByEventIdOneUser(id: String, userId: String): Future[Seq[Bet]] = bucket.getBetByEventIdOneUser(id, userId)

}
