package db.buckets.reports

import com.couchbase.client.scala.{AsyncCluster, AsyncCollection}
import db.buckets.bets.BetBucket
import entity.CompanyReport

import scala.concurrent.{ExecutionContext, Future}

class ReportBucket(cluster: AsyncCluster, betBucket: BetBucket)(implicit ex: ExecutionContext) {

  final val bucket = cluster.bucket("CompanyReport")
  final val defaultCollection: AsyncCollection = bucket.defaultCollection

  def createReport(sport: String): Future[CompanyReport] = {
    for {
      numOfBets <- betBucket.getNumOfBetsBySport(sport)
      totalAmount <- betBucket.totalAmountFromBets(sport)
      payoutAmount <- betBucket.totalAmountFromWinBets(sport)
      income <- betBucket.incomeFromBetsBySport(sport)
      id = sport
      record = CompanyReport(id, sport, numOfBets, totalAmount, payoutAmount, income)
      db <- defaultCollection.upsert(id, record)(CompanyReport.codec).map(_ => record)
    } yield db

  }


  def getAllReports: Future[Seq[CompanyReport]] = {
    val query =
      """SELECT cr.* FROM `CompanyReport` cr"""

    cluster.query(query).map(_.rowsAs(CompanyReport.codec).getOrElse(Seq()).toSeq)
  }
}