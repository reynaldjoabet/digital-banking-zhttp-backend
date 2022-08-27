package model

import zio.json._
import zio._

final case class Customer(
  customerId: Int, // customer id
  name: String,
  email: String,
)

object Customer {
  implicit val codec: JsonCodec[Customer] = DeriveJsonCodec.gen[Customer]

  def make(
    name: String,
    email: String,
  ): ZIO[Any, Nothing, Customer] = Random.nextIntBetween(1, 1000).map(Customer(_, name, email))

}
