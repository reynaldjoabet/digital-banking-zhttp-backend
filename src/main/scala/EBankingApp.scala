
import zio._
import configurations.AppConfig
import db.QuillContext
import model.OperationType
import services._
import zhttp.service.Server
import zhttp.http.Middleware._

object EBankingApp  extends ZIOAppDefault{

    override def run: ZIO[Environment with ZIOAppArgs with Scope,Any,Any] =
        (for{

            service<-ZIO.service[MigrationService]
            _   <- service.reset
            _   <- service.migrate
            accountService<-ZIO.service[BankAccountService]
            //accountOperationService<-ZIO.service[AccountOperationService]
            customerService<-ZIO.service[CustomerService]
           _ <- customerService.populateCustomerDb()
            customers <- customerService.findAll()
            _         <- ZIO.foreach(customers)(customer =>accountService.createCurrentAccount( balance=9000,customerId = customer.customerId,overDraft = Some(800.0)))
            _         <- ZIO.foreach(customers)(customer =>accountService.createSavingAccount( balance=34000,customerId = customer.customerId,interestRate= Some(5.5)))

            accounts  <-accountService.findAll()
            amount    <-Random.nextIntBetween(1,20000)
           _        <-ZIO.foreach(accounts)(account=>accountService.credit(account.accountId,10000+amount,"Credit"))
            amount1    <-Random.nextIntBetween(1,190)
            _        <-ZIO.foreach(accounts)(account=>accountService.debit(account.accountId,amount=amount1,"Debit"))

            port <- System.envOrElse("PORT", "8080").map(_.toInt)
            //_    <- Server.start(port,  Middleware.cors() @@ loggingMiddleware)

        } yield ()).provide(QuillContext.dataSourceLayer,MigrationService.layer,BankAccountService.bankAccountLayer, CustomerService.layer,AccountOperationService.layer)
  
}
