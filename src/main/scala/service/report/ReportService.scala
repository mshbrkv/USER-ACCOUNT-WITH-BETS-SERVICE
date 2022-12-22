package service.report

import entity.CompanyReport

import scala.concurrent.Future

trait ReportService {
  def createReport( sport:String):Future[CompanyReport]
  def getAllReports:Future[Seq[CompanyReport]]

}
