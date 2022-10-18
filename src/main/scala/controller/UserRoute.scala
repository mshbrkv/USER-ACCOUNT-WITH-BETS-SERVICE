package controller

import _root_.entity.User
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax.EncoderOps
import service.bet.BetService
import service.user.UserService

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class UserRoute(userService: UserService, betService: BetService)
               (implicit ex: ExecutionContext) extends FailFastCirceSupport {

  val routes: Route = pathPrefix("user") {
    path("betId" / Segment) {
      betId => {
        get {
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
    } ~ pathPrefix("id" / Segment) { id => {
      //      get {
      //        onComplete(userService.getUserById(id)) {
      //          case Success(value) => {
      //            value match {
      //              case Some(value) => complete(StatusCodes.OK -> value.asJson)
      //              case None => complete(StatusCodes.InternalServerError -> "Missing user")
      //            }
      //          }
      //          case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
      //        }
      //      } ~
      //      path("userBets") {
      //        get {
      //          onComplete(betService.getBetByUserId(id)) {
      //            case Success(value) => complete(StatusCodes.OK -> value.map(_.asJson))
      //            case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
      //          }
      //        }
      //      }
      path("byEventId" / Segment) {
        eventId => {
          get {
            onComplete(betService.getBetByEventIdOneUser(UUID.fromString(eventId), id)) {
              case Success(value) => complete(StatusCodes.OK -> value.map(_.asJson))
              case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
            }
          }
        }
      }
    } ~ delete {
      complete(userService.deleteUser(id))
    }
    } ~ path("active_bets") {
      get {
        onComplete(betService.getActiveBetsByEvent) {
          case Success(value) => complete(StatusCodes.OK -> value)
          case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
        }
      }
    }
  } ~ path("new") {
    entity(as[User]) {
      user => {
        post {
          onComplete(userService.createUser(user)) {
            case Success(value) => complete(StatusCodes.OK -> value)
            case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
          }
        }
      }
    }
  }
}
