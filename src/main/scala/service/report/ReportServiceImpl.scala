package service.report

import db.buckets.reports.ReportBucket
import entity.CompanyReport

import scala.concurrent.{ExecutionContext, Future}

class ReportServiceImpl(val bucket:ReportBucket)(implicit ex: ExecutionContext) extends ReportService {

  override def createReport(id: String, sport: String): Future[CompanyReport] = bucket.createReport(id, sport)
}
