import db.DBConnection.actor
import akka.http.scaladsl.Http
import controller.UserRoute
import service.UserServiceImpl


object UserAccountWithBetsServiceApp extends App {


  val userService = new UserServiceImpl
  val routes = {
    new UserRoute(userService).routes
  }


  Http().newServerAt("127.0.0.1", 8080).bindFlow(routes)

}
