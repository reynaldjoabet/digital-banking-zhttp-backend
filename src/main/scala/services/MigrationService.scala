package services

import org.flywaydb.core.Flyway
import zio._

import javax.sql.DataSource
import configurations.FlywayConfig
import io.getquill.jdbczio.Quill

/** Migrations is a service that uses Flyway to run our migrations.
  *
  * Note that Flyway searches the project's file structure for files that match Flyway's naming
  * convention (see files in db.migration) allowing the user to simply call built-in methods.
  *
  * For more information on Flyway, see: https://flywaydb.org/documentation/
  */
final case class MigrationService(dataSource: DataSource) {

  /** Runs the database migration files.
    */
  val migrate: Task[Unit] =
    for {
      flyway <- loadFlyway
      _ <- ZIO.attempt(flyway.migrate())
    } yield ()

  /** Removes any added data from the database and reruns the migrations effectively resetting it to
    * its original state.
    */
  val reset: Task[Unit] =
    for {
      _ <- ZIO.debug("RESETTING DATABASE!")
      flyway <- loadFlyway
      _ <- ZIO.attempt(flyway.clean())
    } yield ()

  private lazy val loadFlyway: Task[Flyway] = ZIO.attempt {
    Flyway
      .configure()
      .dataSource(dataSource)
      .baselineOnMigrate(true)
      .baselineVersion("0")
      .load()
  }

}

/** Here in the companion object we define the layer that provides the Migrations service.
  */

object MigrationService {

  val layer: ZLayer[DataSource, Nothing, MigrationService] = ZLayer.fromFunction(
    MigrationService.apply _
  )

  def migrate(): RIO[FlywayConfig, Unit] = ZIO.serviceWithZIO[FlywayConfig] { config =>
    for {
      flyway <- ZIO.attempt(
        Flyway.configure().dataSource(config.url, config.username, config.password).load()
      )
      _ <- ZIO.attempt(flyway.migrate())
    } yield ()
  }

  def reset: RIO[FlywayConfig, Unit] = ZIO.serviceWithZIO[FlywayConfig] { config =>
    for {
      _ <- ZIO.debug("RESETTING DATABASE!")
      flyway <- ZIO.attempt(
        Flyway.configure().dataSource(config.url, config.username, config.password).load()
      )
      _ <- ZIO.attempt(flyway.clean())
    } yield ()
  }

}
