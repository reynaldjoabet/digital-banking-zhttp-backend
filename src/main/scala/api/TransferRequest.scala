package api

import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

final case class TransferRequest(
  accountSource: String,
  accountDestination: String,
  amount: Double,
  description: String,
)

object TransferRequest {
  implicit val codec: JsonCodec[TransferRequest] = DeriveJsonCodec.gen[TransferRequest]
}
