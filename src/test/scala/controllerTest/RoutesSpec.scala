package controllerTest

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import controller.Routes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import entity.{Bet, Event, User}
import io.circe.syntax.EncoderOps
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import service.bet.BetService
import service.event.EventService
import service.user.UserService

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps

class RoutesSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest with FailFastCirceSupport {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  val userService: UserService = mock[UserService]
  val betsService: BetService = mock[BetService]
  val eventService: EventService = mock[EventService]
  val routesClas = new Routes(userService, betsService, eventService)

  val routes: Route = routesClas.routes

  val testUser: User = User("003", "", "", "", 100)
  val testUserId: String = testUser.id
  val testEvent: Event = Event(id = "001", "aaa", "ssss", inPlay = true)
  val testBet: Bet = Bet("001", eventId = "001", "003", "aaa", 11)


  it should "return user by id if user exists cas Success" in {
    when(userService.getUserById(testUserId)).thenReturn(Future(Option(testUser)))
    Get(s"/user/id/$testUserId") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual testUser.asJson.noSpaces
    }
  }

  it should "return user by id if user doesnt exist case Success" in {
    when(userService.getUserById("000")).thenReturn(Future(Option.empty))
    Get(s"/user/id/000") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
    }
  }

  it should "return user by id case Failure" in {
    when(userService.getUserById("000")).thenReturn(Future.failed(new Throwable))
    Get(s"/user/id/000") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
    }
  }

  it should "return all bet of user case Success" in {
    when(betsService.getBetByUserId(testUserId)).thenReturn(Future(Seq(testBet)))
    Get(s"/user/id/${testUserId}/user_bets") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldEqual Seq(testBet).asJson.noSpaces
    }
  }

  it should "return all bet of user case Failure" in {
    when(betsService.getBetByUserId("000")).thenReturn(Future.failed(new Throwable))
    Get(s"/user/id/000/user_bets") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
    }
  }

  it should "delete user" in {
    when(userService.deleteUser(testUserId)).thenReturn(Future.successful())
    Delete(s"/user/id/$testUserId") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe "user was removed"
    }
  }
  it should "delete user failed" in {
    when(userService.deleteUser(testUserId)).thenReturn(Future.failed(new Throwable()))
    Delete(s"/user/id/$testUserId") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
    }
  }


  it should "create new bet success" in {
    when(betsService.createBet(testBet, testUserId)).thenReturn(Future(testBet))
    Post(s"/user/id/${testUserId}/new_bet", testBet) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe "bet was created"
    }
  }

  it should "create new bet failed" in {
    when(betsService.createBet(testBet, testUserId)).thenReturn(Future.failed(new Throwable))
    Post(s"/user/id/${testUserId}/new_bet", testBet) ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
      responseAs[String] shouldBe "bet doesnt meet all conditions"
    }
  }

  it should "create new user success" in {
    when(userService.createUser(testUser)).thenReturn(Future(testUser))
    Post("/user/new_user", testUser) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe "user was created"
    }
  }

  it should "create failed" in {
    when(userService.createUser(testUser)).thenReturn(Future.failed(new Throwable()))
    Post("/user/new_user", testUser) ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
    }
  }


  it should "get bet by event id Success" in {
    when(betsService.getBetByEventId("001")).thenReturn(Future(Seq(testBet)))
    Get(s"/bets/by_eventId/001") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe Seq(testBet).asJson.noSpaces
    }
  }

  it should "get bet by event id failed" in {
    when(betsService.getBetByEventId(testEvent.id)).thenReturn(Future.failed(new Throwable))
    Get(s"/bets/by_eventId/${testEvent.id}") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError
    }
  }


  it should "get active bets success" in {
    when(betsService.getActiveBets).thenReturn(Future(Seq(testBet)))
    Get("/bets/active_bets") ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe Seq(testBet).asJson.noSpaces
    }
  }


  it should "get active bets failed" in {
    when(betsService.getActiveBets).thenReturn(Future.failed(new Throwable))
    Get("/bets/active_bets") ~> routes ~> check {
      status shouldBe StatusCodes.InternalServerError

    }
  }
}
