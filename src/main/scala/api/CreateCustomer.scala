package api

import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

final case class CreateCustomer(
     name:String,
    email:String
)

object  CreateCustomer{
    implicit val codec: JsonCodec[CreateCustomer] =
                     DeriveJsonCodec.gen[CreateCustomer]
}
