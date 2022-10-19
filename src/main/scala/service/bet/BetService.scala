package service.bet

import entity.Bet

import scala.concurrent.Future

trait BetService {

  def getBetById(id: String): Future[Option[Bet]]

  def getBetByUserId(id: String): Future[Seq[Bet]]

  def getBetByEventIdOneUser(userId: String, eventId: String): Future[Seq[Bet]]

  def getActiveBets: Future[Seq[Bet]]


}
