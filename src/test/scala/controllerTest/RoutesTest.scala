package controllerTest

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.testkit.{RouteTestResultComponent, ScalatestRouteTest}
import controller.Routes
import entity.User
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import service.bet.BetService
import service.event.EventService
import service.user.UserService

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.postfixOps

class RoutesTest extends AsyncFlatSpec with Matchers with ScalatestRouteTest {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  val userService: UserService = mock[UserService]
  val betsService: BetService = mock[BetService]
  val eventService: EventService = mock[EventService]
  val routes: Routes = Routes(userService, betsService, eventService)
  val testUserId = "003"
  val testUser: User = User("003","","","",100)


  it should "return user by id" in {
    Get(s"/id/${testUserId}") ~> routes ~> check{
      status shouldBe Ok
   responseAs[User] shouldEqual testUser
    }

  }


}
