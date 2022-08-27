package model

import io.getquill.MappedEncoding
import zio.json._

sealed trait AccountType

object AccountType {
  case object CurrentAccount extends AccountType
  case object SavingAccount extends AccountType

  def fromString(str: String): AccountType =
    str match {
      case "CurrentAccount" => CurrentAccount
      case "SavingAccount"  => SavingAccount

    }

  implicit val codec: JsonCodec[AccountType] = DeriveJsonCodec.gen[AccountType]

  implicit val encodeCurrency: MappedEncoding[AccountType, String] =
    MappedEncoding[AccountType, String](_.toString)

  implicit val decodeCurrency: MappedEncoding[String, AccountType] =
    MappedEncoding[String, AccountType](AccountType.fromString(_))
}
