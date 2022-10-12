package db.buckets


import scala.concurrent.Future

abstract class AbstractBucket[T] {

  def getById(id: String): Future[Option[T]]

  def save(data: T): Future[T]

  def deleteById(id: String): Future[Unit]

  protected def entityToId(data: T): String

  protected def convertIdToDocId(id: String): String

}
