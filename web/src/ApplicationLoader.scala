import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}
import domain.{Actor, GreatestTvSeries}
import repositories.RepoSqlContext
import repositories.RepoSqlContext.DbConf
import services.MovieService
import utils.DataFeeder

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

/** *
  * Application starter program.
  *
  * On start, we feed database with csv data using alpakka (akka-stream). First use of this library and i really like it.
  * The database is a basic in-memory relational database on postgresSql mode
  * I have tried to implement hexagonal architecture in this project using scala modules/sbt.
  *
  * TODO : 
  * * add http server to expose some routes (play, akka-http ...)
  * * add Tapir to generate openApi specs for new route
  * * add property based tests
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
  lazy val movieService: MovieService = new MovieService(db.principalRepository, db.titleRepository)

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def start(): Unit = {
    println(s"Creating new database schema")
    db.migrate().unsafeRunSync()
    println(s"Loading data")
    dataLoader.loadData().onComplete {
      case Success(_) =>
        println(s"Loading data succeeded 🎉 !")
        println(s"The TV series that has the most episodes is : ${tvSeriesWithGreatNumberOfEpisodes().primaryTitle}")
        println(s"Looking for movie principals ? Enter a movie name : ")
        val movieName = StdIn.readLine()
        println(s"Searching for $movieName principals ...")
        println(principalsForMovieName(movieName))
        system.terminate()
      case Failure(e) => println(s"Failure :( : $e")
    }

  }

  def tvSeriesWithGreatNumberOfEpisodes(): GreatestTvSeries = {
    val source: Source[GreatestTvSeries, NotUsed] = movieService.tvSeriesWithGreatNumberOfEpisodes()
    val sink = Sink.head[GreatestTvSeries]
    // connect the Source to the Sink, obtaining a RunnableGraph
    val runnable: RunnableGraph[Future[GreatestTvSeries]] = source.toMat(sink)(Keep.right)
    // materialize the flow and get the value of the sink.head
    Await.result(runnable.run(), Duration(5, TimeUnit.SECONDS))
  }

  def principalsForMovieName(movieName: String): Seq[Actor] = {
    val source = movieService.principalsForMovieName(movieName)
    val res: Future[Seq[Actor]] = source.runWith(Sink.seq[Actor])
    Await.result(res, Duration(5, TimeUnit.SECONDS))
  }
}
