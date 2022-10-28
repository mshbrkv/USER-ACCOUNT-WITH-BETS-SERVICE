package dbTest.bucketTest

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.couchbase.client.scala.kv.MutationResult
import com.couchbase.client.scala.{AsyncBucket, AsyncCluster, AsyncCollection}
import db.buckets.bets.BetBucket
import entity.{Bet, Event}
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import service.event.EventService

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

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
}
