package controller

import _root_.entity.{Bet, User}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax.EncoderOps
import service.bet.BetService
import service.user.UserService

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Routes(userService: UserService, betService: BetService)
            (implicit ex: ExecutionContext) extends FailFastCirceSupport {

  private val routesWithUserId: Route = pathPrefix("user") {
    pathPrefix("id" / Segment) { id => {
      get { //work
        pathEndOrSingleSlash {
          onComplete(userService.getUserById(id)) {
            case Success(value) =>
              value match {
                case Some(value) => complete(StatusCodes.OK -> value.asJson)
                case None => complete(StatusCodes.InternalServerError)
              }
            case Failure(exception) => complete(StatusCodes.InternalServerError)
          }
        }
      } ~
        path("user_bets") { //work
          get {
            onComplete(betService.getBetByUserId(id)) {
              case Success(value) => complete(StatusCodes.OK -> value.map(_.asJson))
              case Failure(exception) => complete(StatusCodes.InternalServerError)
            }
          }
        } ~
        delete { //work
          onComplete(userService.deleteUser(id)) {
            case Success(value) => complete(StatusCodes.OK -> "user was removed")
            case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
          }
        } ~
        path("new_bet") {
          entity(as[Bet]) {
            bet => {
              post { //work
                onComplete(betService.createBet(bet, id)) {
                  case Success(value) => complete(StatusCodes.OK -> "bet was created")
                  case Failure(exception) => complete(StatusCodes.InternalServerError -> "bet doesnt meet all conditions")
                }
              }
            }
          }
        }
    }
    } ~
      path("new_user") { //work
        entity(as[User]) {
          user => {
            post {
              onComplete(userService.createUser(user)) {
                case Success(value) => complete(StatusCodes.OK -> "user was created")
                case Failure(exception) => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      }
  }
  private val betRoutes: Route = pathPrefix("bets") {
    path("betId" / Segment) {
      betId => {
        get {
          pathEndOrSingleSlash {
            //work
            onComplete(betService.getBetById(betId)) {
              case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
              case Success(value) =>
                value match {
                  case Some(value) => complete(StatusCodes.OK -> value.asJson)
                  case None => complete(StatusCodes.InternalServerError -> "Missing bet")
                }
            }
          }
        }
      }
    } ~
      pathPrefix("by_eventId" / Segment) { //work
        eventId => {
          get {
            onComplete(betService.getBetByEventId(eventId)) {
              case Success(value) => complete(StatusCodes.OK -> value)
              case Failure(exception) => complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
  }
  val routes: Route = routesWithUserId ~ betRoutes
}
