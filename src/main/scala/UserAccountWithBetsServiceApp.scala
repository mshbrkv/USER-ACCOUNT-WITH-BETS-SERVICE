import akka.http.scaladsl.Http
import controller.UserRoute
import db.DBConnection
import db.DBConnection.actor
import db.buckets.UserBucket
import service.UserServiceImpl

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}


object UserAccountWithBetsServiceApp extends App {
  val cluster = DBConnection.cluster


  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val bucket = new UserBucket(cluster.async)
  val userService = new UserServiceImpl(bucket)
  val routes = {
    new UserRoute(userService).routes
  }


  Http().newServerAt("0.0.0.0", 8080).bindFlow(routes)

}
