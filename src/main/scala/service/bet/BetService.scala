package service.bet

import entity.{Bet, Event}
import ujson.Value

import java.util.UUID
import scala.concurrent.Future

trait BetService {

  def getBetById(id: String): Future[Option[Bet]]

  def getBetByUserId(id: String): Future[Seq[Bet]]

  def getBetByEventIdOneUser(userId: String,eventId: String): Future[Seq[Bet]]


}
