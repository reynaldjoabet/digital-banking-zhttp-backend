
import zio._
import configurations.AppConfig
import db.QuillContext
import model.OperationType
import services._
object EBankingApp  extends ZIOAppDefault{

    override def run: ZIO[Environment with ZIOAppArgs with Scope,Any,Any] =
        (for{

            service<-ZIO.service[MigrationService]
            _   <- service.reset
            _   <- service.migrate
            accountService<-ZIO.service[BankAccountService]
            accountOperationService<-ZIO.service[AccountOperationService]
            customerService<-ZIO.service[CustomerService]
           _ <- customerService.populateCustomerDb()
            customers <- customerService.findAll()
            _         <- ZIO.foreach(customers)(customer =>accountService.createCurrentAccount( balance=9000,customerId = customer.customerId,overDraft = Some(800.0)))
            _         <- ZIO.foreach(customers)(customer =>accountService.createSavingAccount( balance=34000,customerId = customer.customerId,interestRate= Some(5.5)))

            accounts  <-accountService.findAll()
            amount    <-Random.nextIntBetween(1,20000)
            _        <-ZIO.foreach(accounts)(account=>accountOperationService.create(amount=amount,operationType = OperationType.DEBIT,account.accountId))
            amount1    <-Random.nextIntBetween(1,19000)
            _        <-ZIO.foreach(accounts)(account=>accountOperationService.create(amount=amount1,operationType = OperationType.CREDIT,account.accountId))

        } yield ()).provide(QuillContext.dataSourceLayer,MigrationService.layer,BankAccountService.layer, CustomerService.layer,AccountOperationService.layer)
  
}
