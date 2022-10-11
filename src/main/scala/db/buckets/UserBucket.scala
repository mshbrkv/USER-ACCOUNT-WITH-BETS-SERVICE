package db.buckets

import com.couchbase.client.scala.kv.GetResult
import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, AsyncCollection}
import entity.User

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UserBucket(cluster: AsyncCluster)
                (implicit ex: ExecutionContext) extends AbstractBucket[User] {

  private val bucket: AsyncBucket = cluster.bucket("User")
  private val defaultCollection: AsyncCollection = bucket.defaultCollection


  override def getById(id: UUID): Future[Option[User]] = {
    defaultCollection.get(id.toString)
      .map(asObject)
  }

  private def asObject(doc: GetResult): Option[User] = {
    doc.contentAs[String] match {
      case Failure(exception) => throw exception
      case Success(value) => io.circe.parser.decode[User](value)
    }
  }.toOption

  override def save(data:User): Future[User] = {

//    Future.successful(User(UUID.randomUUID(), "", "", "", BigDecimal.valueOf(1), List()))
  }

  override def query(): AsyncBucket = {
//    defaultCollection.
  }
}
