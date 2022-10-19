import akka.http.scaladsl.Http
import controller.Routes
import db.DBConnection
import db.DBConnection.actor
import db.buckets.bets.BetBucket
import db.buckets.user.UserBucket
import service.bet.BetServiceImpl
import service.event.EventServiceImpl
import service.user.UserServiceImpl

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}


object UserAccountWithBetsServiceApp extends App {
  val cluster = DBConnection.cluster

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val bucketUser = new UserBucket(cluster.async)
  val bucketBet = new BetBucket(cluster.async)
  val userService = new UserServiceImpl(bucketUser)
  val betService = new BetServiceImpl(bucketBet)
  val eventService = new EventServiceImpl()

  val routes = {
    new Routes(userService, betService, eventService).routes
  }

  Http().newServerAt("0.0.0.0", 8080).bindFlow(routes)

}
