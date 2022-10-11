package entity

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.util.UUID

final case class User(id: UUID, firstName: String, lastName: String, email: String, balance: BigDecimal, bets: List[Bet])


object User {
  implicit val decoder: Decoder[User] = deriveDecoder
  implicit val encoder: Encoder[User] = deriveEncoder
}

