package entity

import com.couchbase.client.scala.implicits.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.util.UUID

final case class Event(id: String, name: String, startTime: String, inPlay: Boolean)

object Event {
  implicit val codec: Codec[Event] = Codec.codec[Event]
  implicit val decoder: Decoder[Event] = deriveDecoder
  implicit val encoder: Encoder[Event] = deriveEncoder
}