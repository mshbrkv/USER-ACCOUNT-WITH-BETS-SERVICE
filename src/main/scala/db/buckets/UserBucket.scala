package db.buckets


import com.couchbase.client.scala.kv.GetResult
import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, AsyncCollection}
import entity.User

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UserBucket(cluster: AsyncCluster)
                (implicit ex: ExecutionContext) extends AbstractBucket[User] {


  private val bucket: AsyncBucket = cluster.bucket("User")
  private val defaultCollection: AsyncCollection = bucket.defaultCollection

  override def getById(id: String): Future[Option[User]] = {
    defaultCollection.get(convertIdToDocId(id))
      .map(asObject)
  }

  private def asObject(doc: GetResult): Option[User] = {
    doc.contentAs[io.circe.Json] match {
      case Success(value) => value.as[User]
      case Failure(exception) => throw exception
    }
  }.toOption

  override def convertIdToDocId(id: String): String = {
    s"U:${id}"
  }

  override def save(data: User): Future[User] = {
    defaultCollection.insert(entityToId(data), data)(User.codec).map(_ => data)
  }

  override def entityToId(data: User): String = {
    s"U:${data.id}"
  }

  override def deleteById(id: String): Future[Unit] = {
    defaultCollection.remove(convertIdToDocId(id)).map(_ => ())
  }
}
