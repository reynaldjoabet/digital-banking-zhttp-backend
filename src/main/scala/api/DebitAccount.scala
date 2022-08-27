package api

import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

final case class DebitAccount(
  accountId: String,
  amount: Double,
  description: String,
)

object DebitAccount {
  implicit val codec: JsonCodec[DebitAccount] = DeriveJsonCodec.gen[DebitAccount]

}
