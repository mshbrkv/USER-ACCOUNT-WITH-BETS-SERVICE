package db.buckets

import com.couchbase.client.scala.Collection

import java.util.UUID
import scala.concurrent.Future

abstract class AbstractBucket[T] {


  def getById(id: UUID): Future[Option[T]]

  def save(data: T): Future[T]

  def query[U](): Future[U]

}
