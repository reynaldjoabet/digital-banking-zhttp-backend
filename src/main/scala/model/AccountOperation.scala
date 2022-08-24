package model


import zio.json._
import zio._
import java.time.LocalDateTime
final case class AccountOperation(
id:Long,
operationDate:LocalDateTime,
amount:Double,
operationType:OperationType,
bankAccountId:String,// account id
 description:String
)


object  AccountOperation{
implicit val codec: JsonCodec[AccountOperation] =
            DeriveJsonCodec.gen[AccountOperation]

def make(
    amount:Double,
    operationType:OperationType,
    bankAccountId:String,
    description:String
): ZIO[Any,Nothing,AccountOperation]=
  Random.nextIntBetween(1,10000).map(AccountOperation(_,LocalDateTime.now(),amount,operationType,bankAccountId,description))


}