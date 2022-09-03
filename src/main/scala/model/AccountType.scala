package model

import io.getquill.MappedEncoding
import zio.json._
import model.AccountType.CurrentAccount
import model.AccountType.SavingAccount

sealed trait AccountType {

  override def toString(): String =
    this match {
      case CurrentAccount => "CurrentAccount"
      case SavingAccount  => "SavingAccount"
    }

}

object AccountType {
  case object CurrentAccount extends AccountType
  case object SavingAccount extends AccountType

  // implicit val codec: JsonCodec[AccountType] = DeriveJsonCodec.gen[AccountType]

  implicit def accountTypeToString(accountType: AccountType): String = accountType.toString()

  implicit def fromString(str: String): AccountType =
    str match {
      case "CurrentAccount" => CurrentAccount
      case "SavingAccount"  => SavingAccount
    }

}
