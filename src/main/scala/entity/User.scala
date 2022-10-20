package entity

import com.couchbase.client.scala.implicits.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}


final case class User(id: String, firstName: String, lastName: String, email: String, balance: BigDecimal)


object User {
  implicit val codec: Codec[User] = Codec.codec[User]
  implicit val decoder: Decoder[User] = deriveDecoder
  implicit val encoder: Encoder[User] = deriveEncoder
}

