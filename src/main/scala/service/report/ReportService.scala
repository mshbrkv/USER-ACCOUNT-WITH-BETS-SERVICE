package service.report

import entity.CompanyReport

import scala.concurrent.Future

trait ReportService {
  def createReport(id:String, sport:String):Future[CompanyReport]

}
