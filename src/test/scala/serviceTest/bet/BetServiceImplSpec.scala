package serviceTest.bet

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.buckets.bets.BetBucket
import entity.Bet
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import service.bet.BetServiceImpl

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class BetServiceImplSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest {

  implicit val ex: ExecutionContextExecutor = ExecutionContext.global
  val betBucket: BetBucket = mock[BetBucket]
  val betService: BetServiceImpl = new BetServiceImpl(betBucket)
  val testBet: Bet = Bet("001", "001", "001", "aaa", 100)
  val testBetId: String = "001"
  val testUserId: String = "001"
  val testEventId: String = "001"

  it should "get bet by Id" in {
    when(betBucket.getById(testBetId)).thenReturn(Future(Option(testBet)))
    val actual = betService.getBetById(testBetId)
    assert(actual.futureValue == Option(testBet))
  }

  it should "get bet by user id" in {
    when(betBucket.getBetByUserId(testUserId)).thenReturn(Future(Seq(testBet)))
    val actual = betService.getBetByUserId(testUserId)
    assert(actual.futureValue == Seq(testBet))
  }


  it should "get bet by event id" in {
    when(betBucket.getBetByEventId(testEventId)).thenReturn(Future(Seq(testBet)))
    val actual = betService.getBetByEventId(testEventId)
    assert(actual.futureValue == Seq(testBet))
  }

  it should "get active bets" in{
    when(betBucket.getActiveBets).thenReturn(Future(Seq(testBet)))
    val actual = betService.getActiveBets
    assert(actual.futureValue==Seq(testBet))
  }

  it should "create bet" in{
    when(betBucket.createBet(testBet,testUserId)).thenReturn(Future(testBet))
    val actual=betService.createBet(testBet, testUserId)
    assert(actual.futureValue==testBet)
  }
}
