package services

import zio._
import db.QuillContext
import javax.sql.DataSource
import model.Customer

trait CustomerService {

  def createCustomer(
    name: String,
    email: String,
  ): Task[Customer]

  def populateCustomerDb(): Task[Unit]

  def findAll(): Task[List[Customer]]
  def get(customerId: Int): Task[Option[Customer]]

  def update(customerId: Int, name: Option[String], email: Option[String]): Task[Unit]
  def delete(customerId: Int): Task[Unit]
}

/** CustomerServiceLive is a service which provides the "live" implementation of the CustomerService
  * . This implementation uses a DataSource, which will concretely be a connection pool.
  */
final case class CustomerServiceLive(dataSource: DataSource) extends CustomerService {

  // QuillContext needs to be imported here to expose the methods in the QuillContext object.
  import QuillContext._

  override def createCustomer(name: String, email: String): Task[Customer] =
    for {
      customer <- Customer.make(name, email)
      _ <- run(query[Customer].insertValue(lift(customer)))
        .provideEnvironment(ZEnvironment(dataSource))

    } yield customer

  override def populateCustomerDb(): Task[Unit] = {
    val customers = List(
      Customer(1, "Hassan", "Hassan@gmail.com"),
      Customer(2, "Yassine", "Yassine@gmail.com"),
      Customer(4, "Aicha", "Aich@gmail.com"),
      Customer(3, "Mohamed", "mohamed@gmail.com"),
    )
    run {
      quote {
        liftQuery(customers).foreach { customer =>
          query[Customer].insertValue(customer)
        }

      }
    }.provideEnvironment(ZEnvironment(dataSource))
  }.unit

  override def findAll(): Task[List[Customer]] = run(query[Customer])
    .provideEnvironment(ZEnvironment(dataSource))

  override def get(
    customerId: Int
  ): Task[Option[Customer]] = run(query[Customer].filter(_.customerId == lift(customerId)))
    .provideEnvironment(ZEnvironment(dataSource))
    .map(_.headOption)

  override def update(customerId: Int, name: Option[String], email: Option[String]): Task[Unit] =
    run(
      dynamicQuery[Customer]
        .filter(_.customerId == lift(customerId))
        .update(setOpt(_.name, name), setOpt(_.email, email))
    )
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

  override def delete(customerId: Int): Task[Unit] =
    run(query[Customer].filter(_.customerId == lift(customerId)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

}

object CustomerService {

  val layer: ZLayer[DataSource, Nothing, CustomerServiceLive] = ZLayer.fromFunction(
    CustomerServiceLive.apply _
  )

}
