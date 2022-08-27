package model

import zio.json._
import io.getquill.MappedEncoding
sealed trait AccountStatus

object AccountStatus {
  implicit val codec: JsonCodec[AccountStatus] = DeriveJsonCodec.gen[AccountStatus]
  DeriveJsonCodec

  case object CREATED extends AccountStatus
  case object ACTIVATED extends AccountStatus
  case object SUSPENDED extends AccountStatus

  def fromString(str: String): AccountStatus =
    str match {
      case "CREATED"   => CREATED
      case "ACTIVATED" => ACTIVATED
      case "SUSPENDED" => SUSPENDED
    }

  implicit val encodeCurrency: MappedEncoding[AccountStatus, String] =
    MappedEncoding[AccountStatus, String](_.toString)

  implicit val decodeCurrency: MappedEncoding[String, AccountStatus] =
    MappedEncoding[String, AccountStatus](AccountStatus.fromString(_))

}
