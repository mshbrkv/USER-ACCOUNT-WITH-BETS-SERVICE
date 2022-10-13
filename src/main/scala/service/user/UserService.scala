package service.user

import entity.User

import scala.concurrent.Future

trait UserService {
  def getUserById(id: String):  Future[Option[User]]

  def createUser(user: User): Future[User]

  def deleteUser(id: String): Future[Unit]
}
