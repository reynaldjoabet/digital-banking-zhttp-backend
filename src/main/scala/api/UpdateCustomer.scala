package api

import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

final case class UpdateCustomer(
    name:Option[String],
       email:Option[String]
)


object  UpdateCustomer{
    implicit val codec: JsonCodec[UpdateCustomer] =
                         DeriveJsonCodec.gen[UpdateCustomer]
    
}
