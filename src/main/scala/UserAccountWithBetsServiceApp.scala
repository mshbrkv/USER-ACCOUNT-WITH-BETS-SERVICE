import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import config.ConsumerConfiguration
import controller.Routes
import db.DBConnection
import db.buckets.bets.BetBucket
import db.buckets.user.UserBucket
import service.bet.BetServiceImpl
import service.event.EventServiceImpl
import service.user.UserServiceImpl

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object UserAccountWithBetsServiceApp extends App {
  val cluster = DBConnection.cluster
  implicit val system: ActorSystem = ActorSystem("bet-service")

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global


  val bucketUser = new UserBucket(cluster.async)
  val eventService = new EventServiceImpl()
  val bucketBet = new BetBucket(cluster.async, eventService)
  val userService = new UserServiceImpl(bucketUser)
  val betService = new BetServiceImpl(bucketBet)
  val consumer = new ConsumerConfiguration()

  val routesForBetAndUser = {
    new Routes(userService, betService).routes
  }

  Http().newServerAt("0.0.0.0", 8080).bindFlow(routesForBetAndUser)


}