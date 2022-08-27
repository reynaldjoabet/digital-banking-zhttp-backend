package model

import zio.json._

final case class AccountOperationId(id: Long) extends AnyVal

object AccountOperationId {

  implicit val codec: JsonCodec[AccountOperationId] = JsonCodec[Long].transform(
    AccountOperationId(_),
    _.id,
  )

}
