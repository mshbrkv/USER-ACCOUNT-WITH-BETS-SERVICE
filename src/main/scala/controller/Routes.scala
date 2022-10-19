package controller

import _root_.entity.User
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax.EncoderOps
import service.bet.BetService
import service.event.EventService
import service.user.UserService

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Routes(userService: UserService, betService: BetService, eventService: EventService)
            (implicit ex: ExecutionContext) extends FailFastCirceSupport {



  private val routesWithUserId: Route = pathPrefix("user") {
    pathPrefix("id" / Segment) { id => {
      get { //work
        pathEndOrSingleSlash {
          onComplete(userService.getUserById(id)) {
            case Success(value) => {
              value match {
                case Some(value) => complete(StatusCodes.OK -> value.asJson)
                case None => complete(StatusCodes.InternalServerError -> "Missing user")
              }
            }
            case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
          }
        }
      } ~
        pathPrefix("byEventId" / Segment) { //work
          eventId => {
            get {
              onComplete(betService.getBetByEventIdOneUser(id, eventId)) {
                case Success(value) => complete(StatusCodes.OK -> value.map(_.asJson))
                case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
              }
            }
          }
        } ~
        path("userBets") { //empty
          get {
            onComplete(betService.getBetByUserId(id)) {
              case Success(value) => complete(StatusCodes.OK -> value.map(_.asJson))
              case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
            }
          }
        } ~
        delete { //work
          onComplete(userService.deleteUser(id)) {
            case Success(value) => complete(StatusCodes.OK -> "done")
            case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
          }
        }
    }
    } ~
      path("new") { //work
        entity(as[User]) {
          user => {
            post {
              onComplete(userService.createUser(user)) {
                case Success(value) => complete(StatusCodes.OK -> "create")
                case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
              }
            }
          }
        }
      }
  }
  private val eventsRoutes = pathPrefix("events") {
    path("activeEvents") { //work
      get {
        onComplete(eventService.getActiveBetsByEvent) {
          case Success(value) => complete(StatusCodes.OK -> value)
          case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
        }
      }
    }
  }
  private val betRoutes: Route = pathPrefix("bets") {
    path("betId" / Segment) {
      betId => {
        get { //work
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
    } ~
      path("activeBets") { //work
        get {
          onComplete(betService.getActiveBets) {
            case Success(value) => complete(StatusCodes.OK -> value)
            case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
          }
        }
      }
  }

  val routes: Route = routesWithUserId ~ betRoutes ~ eventsRoutes

}
