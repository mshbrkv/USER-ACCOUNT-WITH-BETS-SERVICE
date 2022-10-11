package entity

import java.util.UUID

final case class Bet(id: UUID, name: String, price: BigDecimal, event: Object)

