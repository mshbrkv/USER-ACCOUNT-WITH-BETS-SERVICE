package service.bet

import entity.Bet

import scala.concurrent.Future

trait BetService {

  def getBetById(id: String): Future[Option[Bet]]

  def getBetByUserId(id: String): Future[Seq[Bet]]

  def getBetByEventId(eventId: String): Future[Seq[Bet]]

  def createBet(data: Bet, userId: String): Future[Bet]


}
