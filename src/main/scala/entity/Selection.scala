package entity

import com.couchbase.client.scala.implicits.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Selection(id: String, name: String, sport:String, price: Double, market: String, result: String)

object Selection {
  implicit val codec: Codec[Selection] = Codec.codec[Selection]
  implicit val decoder: Decoder[Selection] = deriveDecoder
  implicit val encoder: Encoder[Selection] = deriveEncoder
}