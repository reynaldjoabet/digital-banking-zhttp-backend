package model

import zio.json._

final case class AccountId(id: Long) extends AnyVal

object AccountId {

  implicit val codec: JsonCodec[AccountId] = JsonCodec[Long].transform(AccountId(_), _.id)
}
