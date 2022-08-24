package configurations
import zio.TaskLayer
import zio.config.ConfigDescriptor._
import zio.config.magnolia.descriptor
import zio.config.syntax._
import zio.config.typesafe.TypesafeConfig
final case class AppConfig(flyway: FlywayConfig)

object  AppConfig{

   private type AllConfig = AppConfig with FlywayConfig

    private  final val Root = "banking"

    private final val Descriptor = descriptor[AppConfig]

    private val appConfig = TypesafeConfig.fromResourcePath(nested(Root)(Descriptor))

    val live: TaskLayer[AllConfig] =
      appConfig >+>
        appConfig.narrow(_.flyway)


}