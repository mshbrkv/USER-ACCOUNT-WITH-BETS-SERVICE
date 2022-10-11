package controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import service.UserService

import scala.util.{Failure, Success}

class UserRoute(userService: UserService) extends FailFastCirceSupport {
  val routes: Route = {
    pathPrefix("id" / Segment) { id =>
      get {
        onComplete(userService.getUserById(id) {
          case Success(value) => complete(StatusCodes.OK -> value)
          case Failure(exception) => complete(StatusCodes.InternalServerError -> exception)
        })
      }
    }
  }
}
