package entity

import com.couchbase.client.scala.implicits.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.joda.time.DateTime


final case class Bet(id: String, sport:String, eventId: String, selectionId: String, userId: String, name: String, price: BigDecimal, coefficient: BigDecimal, status: String, date:String)

object Bet {
  implicit val codec: Codec[Bet] = Codec.codec[Bet]
  implicit val decoder: Decoder[Bet] = deriveDecoder
  implicit val encoder: Encoder[Bet] = deriveEncoder
}

