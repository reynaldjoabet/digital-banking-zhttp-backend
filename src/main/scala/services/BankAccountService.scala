package services
import zio._
import db.QuillContext
import javax.sql.DataSource
import AccountOperationService._
import model._
import model.AppError._

trait BankAccountService {
    def createSavingAccount(
        balance:Double,
         customerId:Int,
        interestRate:Option[Double]
    ): Task[BankAccount]

    def createCurrentAccount(
            balance:Double,
             customerId:Int,
            overDraft:Option[Double]
        ): Task[BankAccount]


def findAll():Task[List[BankAccount]]

def get(accountId:String):Task[Option[BankAccount]]
def delete(accountId:String):Task[Unit]
  def debit(accountId:String,amount:Double,description:String):Task[Unit]
  def credit(accountId:String,amount:Double,description:String):Task[Unit]
  def transfer(accountIdSource:String, accountIdDestination:String,amount:Double): Task[Unit]

}

/** BankAccountServiceLive is a service which provides the "live" implementation of the
  * BankAccountService . This implementation uses a DataSource, which will concretely be
  * a connection pool.
  */
final case class BankAccountServiceLive(dataSource: DataSource,accountOperationService: AccountOperationService) extends BankAccountService {

  // QuillContext needs to be imported here to expose the methods in the QuillContext object.

  import QuillContext._


  override def createSavingAccount(balance: Double, customerId: Int, interestRate: Option[Double]): Task[BankAccount] =
    for {
      savingAccount <- BankAccount.makeSavingAccount(balance, customerId, interestRate)
      _ <- run(query[BankAccount].insertValue(lift(savingAccount))).provideEnvironment(ZEnvironment(dataSource))

    } yield savingAccount

  override def createCurrentAccount(balance: Double, customerId: Int, overDraft: Option[Double]): Task[BankAccount] =
    for {
      currentAccount <- BankAccount.makeCurrentAccount(balance, customerId, overDraft)
      _ <- run(query[BankAccount].insertValue(lift(currentAccount))).provideEnvironment(ZEnvironment(dataSource))

    } yield currentAccount

  override def findAll(): Task[List[BankAccount]] =
    run(query[BankAccount])
      .provideEnvironment(ZEnvironment(dataSource))


  override def get(accountId: String): Task[Option[BankAccount]] =
    run(query[BankAccount].filter(_.accountId == lift(accountId)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)


  override def delete(accountId: String): Task[Unit] =
    run(query[BankAccount].filter(_.accountId == lift(accountId)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit


  override def debit(accountId: String, amount: Double, description: String): Task[Unit] = {
    get(accountId).flatMap{
      case Some(value)if value.balance>amount =>
        val balance=value.balance-amount
        run(
          dynamicQuery[BankAccount]
            .filter(_.accountId == lift(accountId))
            .update(setValue(_.balance,balance))
        )
      case Some(value) if value.balance<amount => throw BalanceNotSufficientException
      case None => throw BankAccountNotFoundException
    }
  }.flatMap(_ =>accountOperationService.create(amount,OperationType.DEBIT,accountId,description))
    .provideEnvironment(ZEnvironment(dataSource))
    .unit


  override def credit(accountId: String, amount: Double, description: String): Task[Unit] = {
    get(accountId).flatMap{
      case Some(value) =>
        val balance=value.balance+amount
        run(
          dynamicQuery[BankAccount]
            .filter(_.accountId == lift(accountId))
            .update(setValue(_.balance,balance))
        )
      case None => throw BankAccountNotFoundException
    }
  }.flatMap(_ =>accountOperationService.create(amount,OperationType.CREDIT,accountId,description))
    .provideEnvironment(ZEnvironment(dataSource))
    .unit


  override def transfer(accountIdSource:String, accountIdDestination:String,amount:Double): Task[Unit]=
    debit(accountIdSource, amount, "Transfer to " + accountIdDestination)
      .*>(credit(accountIdDestination, amount, "transfer from" + accountIdSource)).orElseFail(FailedTransferException)




}




object BankAccountService{

    val bankAccountLayer: ZLayer[DataSource with AccountOperationService, Nothing, BankAccountServiceLive] = ZLayer.fromFunction( BankAccountServiceLive.apply _)




}