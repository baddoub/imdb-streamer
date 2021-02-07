import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import api.HttpServer
import cats.effect.{ExitCode, IO, IOApp}
import repositories.RepoSqlContext.DbConf
import repositories.{DefaultPowerPlantRepository, DefaultStateGenerationRepository, RepoSqlContext}
import utils.DataFeeder

import scala.concurrent.ExecutionContextExecutor

object Application extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    implicit val system: ActorSystem = ActorSystem("g42-actor-system")
    lazy val dbConf: DbConf =
      DbConf("jdbc:h2:mem:imdb_db;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "postgres", "")
    lazy val db: RepoSqlContext = new RepoSqlContext(dbConf)
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val defaultStateGenerationRepository = new DefaultStateGenerationRepository(db.xa)
    implicit val defaultPowerPlantRepository = new DefaultPowerPlantRepository(db.xa)
    val feeder = new DataFeeder()
    val routes = HttpServer.routes()
    (for {
      _ <- db.migrate()
      states <- IO.fromFuture(IO(feeder.loadStatesRef("/csv/states-hash.csv")))
      _ <- IO.fromFuture(IO(feeder.loadPowerPlantsData("/csv/power.plants.csv")))
      _ <- IO.fromFuture(IO(feeder.loadStatesData("/csv/power.plants.csv", states)))
      _ <- IO.fromFuture(IO(Http().newServerAt("0.0.0.0", 9000).bind(routes)))
    } yield ExitCode.Success)
      .handleErrorWith(e => IO(System.err.println(s"Error : ${e}")).map(_ => system.terminate()).as(ExitCode.Error))
  }
}
