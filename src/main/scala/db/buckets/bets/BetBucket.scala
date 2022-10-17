package db.buckets.bets

import com.couchbase.client.scala.kv.GetResult
import com.couchbase.client.scala.{AsyncCluster, AsyncCollection}
import db.buckets.AbstractBucket
import entity.Bet

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


class BetBucket(cluster: AsyncCluster)(implicit ex: ExecutionContext) extends AbstractBucket[Bet] {

  final val bucket = cluster.bucket("Bets")
  final val defaultCollection: AsyncCollection = bucket.defaultCollection


  override def getById(id: String): Future[Option[Bet]] = {
    defaultCollection.get(convertIdToDocId(id)).map(asObject)
  }

  override def asObject(doc: GetResult): Option[Bet] = {
    doc.contentAs[io.circe.Json] match {
      case Success(value) => value.as[Bet]
      case Failure(exception) => throw exception
    }
  }.toOption

  override protected def convertIdToDocId(id: String): String = {
    s"B:${id}"
  }

  override def save(data: Bet): Future[Bet] = defaultCollection.insert(entityToId(data), data)(Bet.codec).map(_ => data)

  override def entityToId(data: Bet): String = {
    s"B:${data.id}"
  }

  override def deleteById(id: String): Future[Unit] = defaultCollection.remove(convertIdToDocId(id)).map(_ => ())

  def getBetByUserId(id: String): Future[Seq[Bet]] = {

    val query =
      s"""
         |SELECT *
         |FROM `Bets` b
         |WHERE b.userId='$id'""".stripMargin


    cluster.query(query)
      .map(_.rowsAs(Bet.codec).getOrElse(Seq()).toSeq)
  }


  def getBetByEventIdOneUser(id: String, userId: String): Future[Seq[Bet]] = {
    val query =
      s"""
         |SELECT *
         |FROM `Bets` b
         |WHERE b.eventId='$id' AND b.userId='$userId'""".stripMargin

    cluster.query(query)
      .map(_.rowsAs(Bet.codec).getOrElse(Seq()).toSeq)
  }
}
