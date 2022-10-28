package db.buckets.user

import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, AsyncCollection}
import db.buckets.AbstractBucket
import entity.User

import scala.concurrent.{ExecutionContext, Future}

class UserBucket(cluster: AsyncCluster)
                (implicit ex: ExecutionContext) extends AbstractBucket[User] {

  private val bucket: AsyncBucket = cluster.bucket("User")
  private val defaultCollection: AsyncCollection = bucket.defaultCollection

  override def getById(id: String): Future[Option[User]] = {
    defaultCollection.get(convertIdToDocId(id)).map(_.contentAs[User].toOption)
  }

  override def convertIdToDocId(id: String): String = {
    s"U:$id"
  }

  def save(data: User): Future[User] = {
    defaultCollection.insert(entityToId(data), data)(User.codec).map(_ => data)
  }

  override def entityToId(data: User): String = {
    s"U:${data.id}"
  }

  override def deleteById(id: String): Future[Unit] = {
    defaultCollection.remove(convertIdToDocId(id)).map(_ => ())
  }
}