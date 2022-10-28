package dbTest.bucketTest

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.couchbase.client.scala.codec.JsonTranscoder
import com.couchbase.client.scala.json.JsonObject
import com.couchbase.client.scala.kv.{GetResult, MutationResult}
import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, AsyncCollection}
import db.buckets.user.UserBucket
import entity.User
import io.circe.syntax.EncoderOps
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.Try

class UserBucketSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest with ScalaFutures with OptionValues {

  implicit val ex: ExecutionContextExecutor = ExecutionContext.global

  val cluster: AsyncCluster = mock[AsyncCluster]
  val bucket: AsyncBucket = mock[AsyncBucket]
  val defaultCollectionMock: AsyncCollection = mock[AsyncCollection]

  when(cluster.bucket("User")).thenReturn(bucket)
  when(bucket.defaultCollection).thenReturn(defaultCollectionMock)

  val userBucket = new UserBucket(cluster)
  val testUser: User = User("001", "", "", "", 100)
  val testUserId: String = "001"
  val mutationResult: MutationResult = mock[MutationResult]
  val getResult: GetResult =mock[GetResult]

  it should "get user by id" in {
    when(defaultCollectionMock.get(anyString(), any[Duration]))
      .thenReturn(Future.successful(getResult))
    when(getResult.contentAs[User]).thenReturn(Try(testUser))
    val actual: Future[Option[User]] = userBucket.getById(testUserId)
    assert(actual.futureValue == Option(testUser))
  }

  it should "save user" in {
    when(defaultCollectionMock.insert("U:001", testUser)(User.codec)).thenReturn(Future.successful(mutationResult))
    val actual = userBucket.save(testUser)
    assert(actual.futureValue == testUser)
  }

  it should "delete by id" in {
    when(defaultCollectionMock.remove("U:001")).thenReturn(Future.successful(mutationResult))
    val actual = userBucket.deleteById("001")
    assert(actual.futureValue == {})
  }

  def getUser(shouldGetValue: Boolean): Either[Array[Byte], JsonObject] = {
    if (shouldGetValue) {
      Left(serialise(Option(testUser)))
    } else {
      Right(JsonObject.fromJson(testUser.asJson.toString()))
    }
  }

  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close()
    stream.toByteArray
  }
}
