import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import repositories.RepoSqlContext
import repositories.RepoSqlContext.DbConf
import services.DefaultMovieService
import utils.DataFeeder

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

/***
  *  Application starter program.
  *
  *  On start, we feed database with csv data using alpakka (akka-stream). First use of this library and i really like it.
  *  The database is a basic in-memory relational database on postgresSql mode
  *  I have tried to implement hexagonal architecture in this project using scala modules/sbt.
  *
  *  TODO : 
  *  * add akka http routes to retrieve data
  *  * add json parsers
  *  * add Tapir to generate openApi specs
  *  * ...
  */
object ApplicationLoader {
  lazy val dbConf: DbConf =
    DbConf("jdbc:h2:mem:imdb_db;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "postgres", "")
  lazy val db: RepoSqlContext = new RepoSqlContext(dbConf)
  lazy val dataLoader: DataFeeder = new DataFeeder(
    db.personRepository,
    db.episodeRepository,
    db.ratingRepository,
    db.principalRepository,
    db.titleRepository,
  )
  lazy val movieService: DefaultMovieService = new DefaultMovieService(db.principalRepository)

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val yay = "\uD83C\uDF89"

  val route: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, "localhost", 8080)

  def start(): Unit = {
    println(s"Creating new database schema")
    db.migrate().unsafeRunSync()
    println(s"Loading data")
    dataLoader.loadData().onComplete {
      case Success(_) =>
        println(s"Loading data succeeded :   $yay")
        movieService.selectAll
          .runWith(Sink.seq)
          .foreach(d => {
            println(s"size ${d.size}")
            d.foreach(p => println(s"principal : $p"))
          })

        println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
        StdIn.readLine() // let it run until user presses return
        ApplicationLoader.bindingFuture
          .flatMap(_.unbind()) // trigger unbinding from the port
          .onComplete(_ => system.terminate()) // and shutdown when done
      case Failure(e) => println(s"Failure :( : $e")
    }

  }
}
