package db.buckets.reports

import com.couchbase.client.scala.{AsyncCluster, AsyncCollection}
import db.buckets.bets.BetBucket
import entity.CompanyReport

import scala.concurrent.{ExecutionContext, Future}

class ReportBucket(cluster: AsyncCluster, betBucket: BetBucket)(implicit ex: ExecutionContext) {

  final val bucket = cluster.bucket("CompanyReport")
  final val defaultCollection: AsyncCollection = bucket.defaultCollection

  def createReport(id: String, sport: String): Future[CompanyReport] = {
    for {numOfBets <- betBucket.getNumOfBetsBySport(sport)
         totalAmount <- betBucket.totalAmountFromBets(sport)
         payoutAmount <- betBucket.totalAmountFromWinBets(sport)
         income <- betBucket.incomeFromBetsBySport(sport)
         record = CompanyReport(entityToId(id), sport, numOfBets, totalAmount, payoutAmount, income)
         db <- defaultCollection.upsert(entityToId(id), record)(CompanyReport.codec).map(_ => record)
         } yield db

  }


  def entityToId(id: String): String = {
    s"CR:${id}"
  }
}