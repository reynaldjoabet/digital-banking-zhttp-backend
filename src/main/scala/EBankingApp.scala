import zio._

import db.QuillContext

import services._

import routes._

object EBankingApp extends ZIOAppDefault {

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = ZIO
    .serviceWithZIO[EbankAppServer](_.start)
    .provide(
      QuillContext.dataSourceLayer,
      MigrationService.layer,
      BankAccountService.bankAccountLayer,
      CustomerService.layer,
      AccountOperationService.layer,
      CustomerRoutes.layer,
      BankAccountRoutes.layer,
      EbankAppServer.serverLayer,
    )

}
