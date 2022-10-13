package db.buckets

import com.couchbase.client.scala.kv.GetResult

import scala.concurrent.Future

abstract class AbstractBucket[T] {

  def getById(id: String): Future[Option[T]]

  def save(data: T): Future[T]

  def deleteById(id: String): Future[Unit]

  def asObject(doc:GetResult):Option[T]

  protected def entityToId(data: T): String

  protected def convertIdToDocId(id: String): String

}
