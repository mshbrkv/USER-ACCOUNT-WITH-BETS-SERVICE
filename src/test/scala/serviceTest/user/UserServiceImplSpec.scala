package serviceTest.user

import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.buckets.user.UserBucket
import entity.User
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import service.user.UserServiceImpl

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class UserServiceImplSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest {

  implicit val ex: ExecutionContextExecutor = ExecutionContext.global
  val userBucket: UserBucket = mock[UserBucket]
  val userService: UserServiceImpl = new UserServiceImpl(userBucket)
  val testUser: User = User("003", "", "", "", 100)
  val testUserId: String = "003"

  it should "getUserById" in {
    when(userBucket.getById(testUserId)).thenReturn(Future(Option(testUser)))
    val actual = userService.getUserById(testUserId)
    assert(actual.futureValue == Option(testUser))
  }

  it should "createUser" in {
    when(userBucket.save(testUser)).thenReturn(Future(testUser))
    val actual = userService.createUser(testUser)
    assert(actual.futureValue == testUser)
  }

  it should "deleteUser" in {
    when(userBucket.deleteById(testUserId)).thenReturn(Future {})
    val actual = userService.deleteUser(testUserId)
    assert(actual.futureValue == {})
  }
}