package controller

import _root_.entity.User
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax.EncoderOps
import service.UserService

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class UserRoute(userService: UserService)
               (implicit ex: ExecutionContext) extends FailFastCirceSupport {

  val routes: Route = pathPrefix("user") {
    path("id" / Segment) { id => {
      onComplete(userService.getUserById(id)) {
        case Success(value) => {
          value match {
            case Some(value) => complete(StatusCodes.OK -> value.asJson)
            case None => complete(StatusCodes.InternalServerError -> "Missing")
          }
        }
        case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
      }
    } ~
      delete {
        complete(userService.deleteUser(id))
      }
    }
  } ~
    path("new") {
      entity(as[User]) { user => {
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
