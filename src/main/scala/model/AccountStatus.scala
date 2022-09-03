package model

import zio.json._
import io.getquill.MappedEncoding
import model.AccountStatus.CREATED
import model.AccountStatus.ACTIVATED
import model.AccountStatus.SUSPENDED

sealed trait AccountStatus {

  override def toString(): String =
    this match {
      case CREATED   => "CREATED"
      case ACTIVATED => "ACTIVATED"
      case SUSPENDED => "SUSPENDED"
    }

}

object AccountStatus {
  // implicit val codec: JsonCodec[AccountStatus] = DeriveJsonCodec.gen[AccountStatus]
  // DeriveJsonCodec

  case object CREATED extends AccountStatus
  case object ACTIVATED extends AccountStatus
  case object SUSPENDED extends AccountStatus

  implicit def accountStatusToString(
    accountStatus: AccountStatus
  ): String = accountStatus.toString()

}
