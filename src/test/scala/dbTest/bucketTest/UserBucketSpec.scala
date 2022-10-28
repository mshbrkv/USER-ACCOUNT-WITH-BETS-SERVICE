package dbTest.bucketTest

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.couchbase.client.scala.codec.JsonTranscoder
import com.couchbase.client.scala.json.JsonObject
import com.couchbase.client.scala.kv.GetResult
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
import org.scalatest.time.{Seconds, Span}

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class UserBucketSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest with ScalaFutures with OptionValues {

  implicit val ex: ExecutionContextExecutor = ExecutionContext.global
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(20, Seconds),
    interval = Span(20, Seconds))

  val cluster: AsyncCluster = mock[AsyncCluster]
  val bucket: AsyncBucket = mock[AsyncBucket]
  val defaultCollectionMock: AsyncCollection = mock[AsyncCollection]

  when(cluster.bucket("User")).thenReturn(bucket)
  when(bucket.defaultCollection).thenReturn(defaultCollectionMock)

  val userBucket = new UserBucket(cluster)
  val testUser: User = User("003", "", "", "", 100)
  val testUserId: String = "003"

  it should "get user by id" in {
    val result = GetResult("", getUser(false), 1, 1, None, JsonTranscoder.Instance)
    val futureResult = Future(result)
    when(defaultCollectionMock.get(anyString(), any(classOf[Duration])))
      .thenReturn(futureResult)

    Thread.sleep(1000)
    val actual: Future[Option[User]] = userBucket.getById(testUserId)
    assert(actual.futureValue == Option(testUser))
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
