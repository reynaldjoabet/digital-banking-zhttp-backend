package routes
import zhttp.http._
import zio._
import zio.json._
import services.CustomerService
import model.AppError._
import  api._

/** CustomerRoutes is a service that provides the routes for the CustumerService API.
  * The routes serve the "customers" endpoint.
  */
final case class CustomerRoutes(service: CustomerService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets all of the Customers in the database and returns them as JSON.
    case Method.GET -> !! / "customers" =>
      service.findAll.map(customers => Response.json(customers.toJson))

    // Gets a single Customer found by their parsed ID and returns it as JSON.
    case Method.GET -> !! / "customers" / customerId =>
      service.get(customerId.toInt).map{
        case None => Response.status(Status.NotFound)
        case Some(value) =>Response.json(value.toJson)
     
      }
        
    // Creates a new Customer from the parsed CreateOwner request body and returns it as JSON.
    case req @ Method.POST -> !! / "customers" =>
      for {
     body   <- req.bodyAsString.orElseFail(MissingBodyError)
     createCustomer <- ZIO.fromEither(body.fromJson[CreateCustomer]).mapError( _ => InvalidJsonBody)
     customer <-service.createCustomer(
                createCustomer.name,
                createCustomer.email
              )
       
      } yield Response.json(customer.toJson).setStatus(Status.Created)

    /** Updates a single Customer found by their parsed ID using the information
      * parsed from the UpdateCustomer request and returns a 200 status code
      * indicating success.
      */
    case req @ Method.PUT -> !! / "customers" / customerId =>
        service.get(customerId.toInt).flatMap{
            case None => ZIO.succeed(Response.status(Status.NotFound))
            case Some(customer) => 
                for {
                        body   <- req.bodyAsString.orElseFail(MissingBodyError)
                        updateCustomer <- ZIO.fromEither(body.fromJson[UpdateCustomer]).mapError( _ => InvalidJsonBody)
                       _ <- service.update(
                            customer.customerId ,
                            updateCustomer.name,
                            updateCustomer.email
                            )
                      } yield Response.ok   
        }
      

    // Deletes a single Customer found by their parsed ID and returns a 200 status code indicating success.
    case Method.DELETE -> !! / "customer" / customerId =>
      for {
         _ <- service.delete(customerId.toInt)
      } yield Response.ok

  }

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the CustomerService API.
  */
object CustomerRoutes {

  val layer: ZLayer[CustomerService,Nothing,CustomerRoutes] = ZLayer.fromFunction(CustomerRoutes.apply _)

}
