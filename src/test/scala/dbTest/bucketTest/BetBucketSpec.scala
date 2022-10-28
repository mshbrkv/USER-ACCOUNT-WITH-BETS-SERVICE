package dbTest.bucketTest

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.couchbase.client.scala.env.{ClusterEnvironment, TimeoutConfig}
import com.couchbase.client.scala.json.JsonObject
import com.couchbase.client.scala.kv.{GetResult, MutationResult}
import com.couchbase.client.scala.query.{QueryParameters, QueryResult}
import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, AsyncCollection}
import db.buckets.bets.BetBucket
import entity.{Bet, Event}
import io.circe.syntax.EncoderOps
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import service.event.EventService

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.Try

class BetBucketSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest with ScalaFutures with OptionValues {

  implicit val ex: ExecutionContextExecutor = ExecutionContext.global

  val cluster: AsyncCluster = mock[AsyncCluster]
  val bucket: AsyncBucket = mock[AsyncBucket]
  val defaultCollectionMock: AsyncCollection = mock[AsyncCollection]

  when(cluster.bucket("Bets")).thenReturn(bucket)
  when(bucket.defaultCollection).thenReturn(defaultCollectionMock)

  val eventService: EventService = mock[EventService]
  val betBucket = new BetBucket(cluster, eventService)
  val mutationResult: MutationResult = mock[MutationResult]
  val testBet: Bet = Bet("001", "001", "001", "aaa", 100)
  val testBetId: String = "001"
  val testUserId: String = "001"
  val testEvent: Event = Event(id = "001", "aaa", "ssss", inPlay = true)
  val testEventId: String = "001"
  val queryResult: QueryResult = mock[QueryResult]
  val env: ClusterEnvironment = ClusterEnvironment.builder
    .timeoutConfig(
      TimeoutConfig()
        .kvTimeout(10.seconds)
    )
    .build
    .get
  val getResult: GetResult = mock[GetResult]

  it should "get bet by id" in {
    when(defaultCollectionMock.get(anyString(), any[Duration]))
      .thenReturn(Future.successful(getResult))
    when(getResult.contentAs[Bet]).thenReturn(Try(testBet))
    val actual: Future[Option[Bet]] = betBucket.getById(testBetId)
    assert(actual.futureValue == Option(testBet))
  }

  it should "save user" in {
    when(eventService.getById(testEventId)).thenReturn(Future.successful(Option(testEvent)))
    when(defaultCollectionMock.insert("B:001", testBet)(Bet.codec)).thenReturn(Future.successful(mutationResult))
    val actual = betBucket.createBet(testBet, testUserId)
    assert(actual.futureValue == testBet)
  }

  it should "delete user by id" in {
    when(defaultCollectionMock.remove("B:001")).thenReturn(Future.successful(mutationResult))
    val actual = betBucket.deleteById("001")
    assert(actual.futureValue == {})
  }

  it should "get bet by userId" in {

    when(cluster.env).thenReturn(env)
    when(cluster.query(anyString(), any[QueryParameters], any[Duration], any[Boolean])).thenReturn(Future(queryResult))

    when(queryResult.rowsAs(Bet.codec)).thenReturn(Try(Seq(testBet)))
    val actual = betBucket.getBetByUserId(testUserId)
    actual.futureValue.head shouldBe testBet
  }

  it should "get bet by eventId" in {
    when(cluster.env).thenReturn(env)
    when(cluster.query(anyString(), any[QueryParameters], any[Duration], any[Boolean])).thenReturn(Future(queryResult))

    when(queryResult.rowsAs(Bet.codec)).thenReturn(Try(Seq(testBet)))
    val actual = betBucket.getBetByEventId(testEventId)
    actual.futureValue.head shouldBe testBet
  }

  def getBet(shouldGetValue: Boolean): Either[Array[Byte], JsonObject] = {
    if (shouldGetValue) {
      Left(serialise(Option(testBet)))
    } else {
      Right(JsonObject.fromJson(testBet.asJson.toString()))
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
