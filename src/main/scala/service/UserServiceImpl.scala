package service

import entity.User

import java.util.UUID
import scala.concurrent.Future

class UserServiceImpl extends UserService {

  override def getUserById(id: UUID): Future[Option[User]] = null

  override def createUser(user: User): Future[User] = null

}
