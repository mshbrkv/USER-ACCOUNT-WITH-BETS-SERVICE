package repository

import entity.User

import java.util.UUID
import scala.concurrent.Future

trait UserRepository {

  def getUserById(id: UUID): Future[Option[User]]

}
