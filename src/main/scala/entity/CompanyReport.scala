package entity

import com.couchbase.client.scala.implicits.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CompanyReport(id: String, sport: String, numberOfBets: Int, totalAmount: BigDecimal, payoutAmount: BigDecimal, income: BigDecimal)
object CompanyReport {
  implicit val codec: Codec[CompanyReport] = Codec.codec[CompanyReport]
  implicit val decoder: Decoder[CompanyReport] = deriveDecoder
  implicit val encoder: Encoder[CompanyReport] = deriveEncoder
}

