package api

import model._
import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

final case class CreateBankAccount(
        balance:Double,
        customerId:Long,
        overDraft:Option[Double],
        interestRate:Option[Double],
        accountType:AccountType
)

object  CreateBankAccount{

    implicit val codec: JsonCodec[CreateBankAccount] =
                   DeriveJsonCodec.gen[CreateBankAccount]
}
