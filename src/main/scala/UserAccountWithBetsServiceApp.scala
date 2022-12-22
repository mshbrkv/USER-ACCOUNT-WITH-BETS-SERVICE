import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import config.ConsumerConfiguration
import controller.Routes
import db.DBConnection
import db.buckets.bets.BetBucket
import db.buckets.reports.ReportBucket
import db.buckets.user.UserBucket
import service.bet.BetServiceImpl
import service.event.EventServiceImpl
import service.report.ReportServiceImpl
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
  val reportBucket = new ReportBucket(cluster.async, bucketBet)
  val reportService = new ReportServiceImpl(reportBucket)
  val consumer = new ConsumerConfiguration(bucketBet)

  val routesForBetAndUser = {
    new Routes(userService, betService, reportService).routes
  }

  Http().newServerAt("0.0.0.0", 8080).bindFlow(routesForBetAndUser)
}