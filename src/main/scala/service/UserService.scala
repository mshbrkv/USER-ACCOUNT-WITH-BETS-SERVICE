package service

import entity.User

import java.util.UUID
import scala.concurrent.Future

trait UserService {
  def getUserById(id: UUID): Future[Option[User]]

  def createUser(user: User): Future[User]
}
