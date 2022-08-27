package api

import zio.json.DeriveJsonCodec
import zio.json.JsonCodec

final case class CreditAccount(
  accountId: String,
  amount: Double,
  description: String,
)

object CreditAccount {
  implicit val codec: JsonCodec[CreditAccount] = DeriveJsonCodec.gen[CreditAccount]
}
