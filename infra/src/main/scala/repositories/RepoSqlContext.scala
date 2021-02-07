package repositories

import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import repositories.RepoSqlContext.DbConf
import utils.FlywayUtils

import scala.concurrent.ExecutionContext

class RepoSqlContext(dbConf: DbConf) {
  private implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val xa: doobie.Transactor[IO] =
    Transactor.fromDriverManager[IO]("org.h2.Driver", dbConf.url, dbConf.user, dbConf.pass)

  private val flyway: Flyway = FlywayUtils.createFlyway(dbConf)

  def migrate(): IO[Int] = IO(flyway.migrate())
  def dropTables(): IO[Unit] = IO(flyway.clean()).map(_ => ())

}

object RepoSqlContext {
  final case class DbConf(url: String, user: String, pass: String)
}
