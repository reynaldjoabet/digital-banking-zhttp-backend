package model

import zio.json._
import zio._
import java.time.LocalDateTime


final case class BankAccount (
   accountId:String,
     balance:Double,
    createdAt:LocalDateTime,
    accountStatus:AccountStatus,
    customerId:Int,
    overDraft:Option[Double],
    interestRate:Option[Double],
    accountType:AccountType
) 


object  BankAccount{



def makeSavingAccount( 
     balance:Double,
    customerId:Int,
   interestRate:Option[Double]): ZIO[Any,Nothing,BankAccount]=
 Random.nextUUID.map(_.toString()).map(BankAccount(_,balance,LocalDateTime.now(),AccountStatus.CREATED,customerId,overDraft=None,interestRate,accountType=AccountType.SavingAccount))


 def makeCurrentAccount( 
      balance:Double,
     customerId:Int,//customer Id
     overDraft:Option[Double]): ZIO[Any,Nothing,BankAccount]=
  Random.nextUUID.map(_.toString()).map(BankAccount(_,balance,LocalDateTime.now(),AccountStatus.CREATED,customerId,overDraft, interestRate=None,accountType=AccountType.CurrentAccount))



implicit val codec: JsonCodec[BankAccount] =
        DeriveJsonCodec.gen[BankAccount]

        DeriveJsonEncoder.toJson()
}
