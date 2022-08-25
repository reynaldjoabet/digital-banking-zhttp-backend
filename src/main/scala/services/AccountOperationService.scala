package services
import zio._
import db.QuillContext
import javax.sql.DataSource
import model._
import model.AppError._

trait AccountOperationService {
    def create(
       amount:Double,
     operationType:OperationType,
     bankAccountId:String,
       description:String
    ): Task[AccountOperation]
  

}


/** AccountOperationService is a service which provides the "live" implementation of the
  * AccountOperationService . This implementation uses a DataSource, which will concretely be
  * a connection pool.
  */
final case class AccountOperationServiceLive(dataSource: DataSource) extends AccountOperationService {

  // QuillContext needs to be imported here to expose the methods in the QuillContext object.
  import QuillContext._


  override def create(amount: Double, operationType: OperationType, bankAccountId: String,description:String): Task[AccountOperation] =
  for {
              operation <-AccountOperation.make(amount,operationType,bankAccountId,description)
              _   <- run(query[AccountOperation].insertValue(lift(operation ))).provideEnvironment(ZEnvironment(dataSource))

            } yield  operation 

}

object  AccountOperationService{

    val layer: ZLayer[DataSource, Nothing, AccountOperationServiceLive] = ZLayer.fromFunction(AccountOperationServiceLive.apply _)
}
