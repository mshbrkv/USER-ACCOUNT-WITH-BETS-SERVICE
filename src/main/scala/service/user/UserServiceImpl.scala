package service.user

import db.buckets.user.UserBucket
import entity.User

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(val bucket: UserBucket)(implicit ex: ExecutionContext) extends UserService {

  override def getUserById(id: String): Future[Option[User]] = bucket.getById(id)

  override def createUser(user: User): Future[User] = bucket.save(user)

  override def deleteUser(id: String): Future[Unit] = bucket.deleteById(id)
}