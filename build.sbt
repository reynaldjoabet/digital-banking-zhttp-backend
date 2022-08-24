ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"
val zioQuillVersion             = "4.3.0"
val postgresVersion             = "42.3.6"
val flywayVersion               = "8.5.12"
val zioJsonVersion              =  "0.3.0-RC11"          
lazy val root = (project in file("."))
  .settings(
    name := "DigitalBankingApp"
  )

libraryDependencies ++=Seq(
  "dev.zio" %% "zio" % "2.0.0",
  "io.d11" %% "zhttp" % "2.0.0-RC10",
  "dev.zio" %% "zio-test" % "2.0.0",
  "io.getquill" %% "quill-jdbc-zio" % zioQuillVersion,
  "org.postgresql"   % "postgresql"   % postgresVersion,
  "org.flywaydb"     % "flyway-core"  % flywayVersion,
  "dev.zio" %% "zio-json" % zioJsonVersion,
  "dev.zio" %% "zio-config" % "3.0.2",
  "dev.zio" %% "zio-config-typesafe" % "3.0.2",
  "dev.zio" %% "zio-config-magnolia" % "3.0.2",
  "dev.zio" %% "zio-logging" % "2.1.0"



)
  
