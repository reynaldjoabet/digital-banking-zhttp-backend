package model

import zio.json.JsonCodec

final case class CustomerId(id:Long) extends AnyVal

object  CustomerId{
    implicit val codec: JsonCodec[CustomerId] =
          JsonCodec[Long].transform(CustomerId(_), _.id)
}
