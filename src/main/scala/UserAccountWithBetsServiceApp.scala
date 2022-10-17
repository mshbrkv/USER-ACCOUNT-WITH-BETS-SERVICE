import akka.http.scaladsl.Http
import controller.UserRoute
import db.DBConnection
import db.DBConnection.actor
import db.buckets.bets.BetBucket
import db.buckets.user.UserBucket
import service.bet.BetServiceImpl
import service.user.UserServiceImpl

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}


object UserAccountWithBetsServiceApp extends App {
  val cluster = DBConnection.cluster


  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val bucketUser = new UserBucket(cluster.async)
  val bucketBet = new BetBucket(cluster.async)
  val userService = new UserServiceImpl(bucketUser)
  val betService = new BetServiceImpl(bucketBet)
  val routes = {
    new UserRoute(userService, betService).routes
  }

  Http().newServerAt("0.0.0.0", 8080).bindFlow(routes)

}
