package entity

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class Bet(id: String, name: String, price: BigDecimal, event: String)

object Bet {
  implicit val codec: Codec[Bet] = deriveCodec
}

