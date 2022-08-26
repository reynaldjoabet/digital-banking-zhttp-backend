
import zio._
import configurations.AppConfig
import db.QuillContext

import services._

import  routes._
import zio.json._

object EBankingApp  extends ZIOAppDefault{

    override def run: ZIO[Environment with ZIOAppArgs with Scope,Any,Any] =
       ZIO.serviceWithZIO[EbankAppServer](_.start)
      .provide(QuillContext.dataSourceLayer,
       MigrationService.layer,
       BankAccountService.bankAccountLayer, 
       CustomerService.layer,
       AccountOperationService.layer,
       CustomerRoutes.layer,
       BankAccountRoutes.layer,
       EbankAppServer.serverLayer 
       )

        sealed trait AccountStatus

            case object CREATED extends AccountStatus

            case object ACTIVATED extends AccountStatus

            case object SUSPENDED extends AccountStatus

            implicit val accountDecoder: JsonDecoder[AccountStatus] = DeriveJsonDecoder.gen[AccountStatus]

            implicit val createdEncoder :JsonEncoder[CREATED.type] = JsonEncoder[String].contramap(_.toString)

            implicit val activatedEncoder: JsonEncoder[ACTIVATED.type] = JsonEncoder[String].contramap(_.toString)

          implicit val suspendedEncoder: JsonEncoder[SUSPENDED.type] = JsonEncoder[String].contramap(_.toString)

         println((CREATED.toJson, ACTIVATED.toJson, SUSPENDED.toJson))

            val result = """
                           |{
                           |"CREATED":"CREATED"
                           |}
                           |""".stripMargin.fromJson[AccountStatus]

            val result2 = """
                            |{
                            |"ACTIVATED":"ACTIVATED"
                            |}
                            |""".stripMargin.fromJson[AccountStatus]

            val result3 = """
                            |{
                            |"SUSPENDE":"SUSPENDED"
                            |}
                            |""".stripMargin.fromJson[AccountStatus]


            println((result, result2, result3))
  
}
