
package routes
import zhttp.http._
import zio._
import zio.json._
import services._
import model.AppError._
import api._
import model.AccountType

/** BankAccountRoutes is a service that provides the routes for the CustumerService API.
  * The routes serve the "customers" endpoint.
  */
final case class BankAccountRoutes(accountService: BankAccountService,accountOperationService: AccountOperationService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets all of the Owners in the database and returns them as JSON.
    case Method.GET -> !! / "accounts" =>
      accountService.findAll.map(accounts => Response.json(accounts.toJson))

//debit
    case req@ Method.POST -> !! / "accounts" /"debit" =>
        for {
              body   <- req.bodyAsString.orElseFail(MissingBodyError)
              debitAccount<- ZIO.fromEither(body.fromJson[DebitAccount]).mapError( _ => InvalidJsonBody)
                    _  <- accountService.debit(debitAccount.accountId,debitAccount.amount,debitAccount.description)
               } yield Response.json(debitAccount.toJson).setStatus(Status.Ok)

               //credit
case req@ Method.POST -> !! / "accounts" /"credit" =>
    for {
          body   <- req.bodyAsString.orElseFail(MissingBodyError)
          creditAccount <- ZIO.fromEither(body.fromJson[CreditAccount]).mapError( _ => InvalidJsonBody)
                _  <- accountService.debit(creditAccount.accountId,creditAccount.amount,creditAccount.description)
           } yield Response.json(creditAccount.toJson).setStatus(Status.Ok)


case req@ Method.POST -> !! / "accounts" /"transfer" =>
    for {

          body   <- req.bodyAsString.orElseFail(MissingBodyError)
          transferRequest<- ZIO.fromEither(body.fromJson[TransferRequest]).mapError( _ => InvalidJsonBody)
                _  <- accountService.transfer(transferRequest.accountSource,transferRequest.accountDestination,transferRequest.amount)
           } yield Response.json(transferRequest.toJson).setStatus(Status.Created)
               
   // Gets a single Account found by their parsed ID and returns it as JSON.
       case Method.GET -> !! / "accounts" / accountId =>
           accountService.get(accountId).map{
               case None => Response.status(Status.NotFound)
               case Some(account) =>  
                   Response.json(account.toJson)   
           }  

 case Method.GET -> !! / "accounts" / accountId/"operations" =>
               accountService.get(accountId).flatMap{
                   case None => ZIO.succeed(Response.status(Status.NotFound))
                   case Some(account) =>  
                       accountOperationService.findAll(accountId)
                       .map(accounts=>Response.json(accounts.toJson))
                          
               }

       //for pagination        
case req @ Method.GET -> !! / "accounts" / accountId/"pageoperations" =>
            accountService.get(accountId).flatMap{
                case None => ZIO.succeed(Response.status(Status.NotFound))
                case Some(account) =>  
                    // not implemented yet
                    accountOperationService.findAll(accountId)
                    .map(accounts=>Response.json(accounts.toJson))

            }

            /**
              * 
              
    // Creates a new Account from the parsed CreateBankAccount request body and returns it as JSON.
    case req @ Method.POST -> !! / "accounts" =>
      for {
      body   <- req.bodyAsString.orElseFail(MissingBodyError)
      jsonBody <- ZIO.fromEither(body.fromJson[CreateBankAccount]).mapError( _ => InvalidJsonBody)
      account <- if(jsonBody.accountType== AccountType.CurrentAccount) accountService.createCurrentAccount(
                 jsonBody.name,
                 jsonBody.email
               ) else accountService.createSavingAccount(
                 jsonBody.name,
                 jsonBody.email
               )

       } yield Response.json(account.toJson).setStatus(Status.Created)

   */

    // Deletes a single Account found by their parsed ID and returns a 200 status code indicating success.
    case Method.DELETE -> !! / "accounts" / accountId =>
     for {
            _ <- accountService.delete(accountId)
         } yield Response.ok

  }

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the BankAccountService API.
  */
object BankAccountRoutes {

  val layer: ZLayer[BankAccountService with AccountOperationService,Nothing,BankAccountRoutes]= ZLayer.fromFunction(BankAccountRoutes.apply _)

}
