
import routes._
import services._
import zhttp.http._
import zhttp.http.middleware.HttpMiddleware
import zhttp.service.Server
import zio._

final case class EbankAppServer(
customerRoutes:CustomerRoutes,
bankAccountRoutes:BankAccountRoutes
) {

  /** Composes the routes together, returning a single HttpApp.
    */
  val allRoutes: HttpApp[Any, Throwable] = {
    customerRoutes.routes++ bankAccountRoutes.routes
  }

  /** Logs the requests made to the server.
    *
    * It also adds a request ID to the logging context, so any further logging
    * that occurs in the handler can be associated with the same request.
    *
    * For more information on the logging, see:
    * https://zio.github.io/zio-logging/
    */
  val loggingMiddleware: HttpMiddleware[Any, Nothing] =
    new HttpMiddleware[Any, Nothing] {
      override def apply[R1 <: Any, E1 >: Nothing](
          http: Http[R1, E1, Request, Response]
      ): Http[R1, E1, Request, Response] =
        Http.fromOptionFunction[Request] { request =>
          Random.nextUUID.flatMap { requestId =>
            ZIO.logAnnotate("REQUEST-ID", requestId.toString) {
              for {
                _      <- ZIO.logInfo(s"Request: $request")
                result <- http(request)
              } yield result
            }
          }
        }
    }



    def start: ZIO[MigrationService with BankAccountService with CustomerService,Throwable,Unit]=
        for{

          service<-ZIO.service[MigrationService]
             _   <- service.reset
             _   <- service.migrate
    accountService<-ZIO.service[BankAccountService]
                   //accountOperationService<-ZIO.service[AccountOperationService]
   customerService<-ZIO.service[CustomerService]
                _ <- customerService.populateCustomerDb()
        customers <- customerService.findAll()
               _  <- ZIO.foreach(customers)(customer =>accountService.createCurrentAccount( balance=9000,customerId = customer.customerId,overDraft = Some(800.0)))
              _   <- ZIO.foreach(customers)(customer =>accountService.createSavingAccount( balance=34000,customerId = customer.customerId,interestRate= Some(5.5)))

        accounts  <-accountService.findAll()
        amount    <-Random.nextIntBetween(1,20000)
         _        <-ZIO.foreach(accounts)(account=>accountService.credit(account.accountId,10000+amount,"Credit"))
       amount1    <-Random.nextIntBetween(1,190)
         _        <-ZIO.foreach(accounts)(account=>accountService.debit(account.accountId,amount=amount1,"Debit"))

             port <- System.envOrElse("PORT", "8080").map(_.toInt)
             _    <- Server.start(port, allRoutes @@ Middleware.cors() @@ loggingMiddleware)

               } yield ()

}

/** Here in the companion object, we define the layer that will be used to
  * create the server.
  */
object EbankAppServer {

  val serverLayer = ZLayer.fromFunction(EbankAppServer.apply _)

}
