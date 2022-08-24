package model
import zio.json._
import io.getquill.MappedEncoding


sealed trait OperationType

object  OperationType{
implicit val codec: JsonCodec[OperationType] =
            DeriveJsonCodec.gen[OperationType]
            
 case object CREDIT extends OperationType 
 case object DEBIT extends OperationType  

 def fromString(str: String):OperationType= str match {
      case "CREDIT"=>CREDIT 
      case "DEBIT"=>DEBIT
    }

implicit val encodeCurrency: MappedEncoding[OperationType,String]=MappedEncoding[OperationType,String](_.toString)

implicit val decodeCurrency: MappedEncoding[String,OperationType]=MappedEncoding[String,OperationType](OperationType.fromString(_)) 
}
