package utils

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Flow, Source}
import akka.util.ByteString
import akka.{Done, NotUsed}
import repositories._
import utils.DataFeeder._

import scala.concurrent.{ExecutionContext, Future}

class DataFeeder(
    personRepository: PersonRepository,
    episodeRepository: EpisodeRepository,
    ratingRepository: RatingRepository,
    principalRepository: PrincipalRepository,
    titleRepository: TitleRepository,
)(implicit executionContext: ExecutionContext, actorSystem: ActorSystem) {

  private def readFile(filePath: String): Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get(filePath))

  private def parse[A](filePath: String)(parser: Map[String, String] => Option[A]): Source[A, Future[IOResult]] = {
    readFile(filePath)
      .via(flow)
      .via(CsvToMap.toMapAsStrings())
      .via(Flow[Map[String, String]].map(parser))
      .collect {
        case Some(v) => v
      }
  }

  def loadPersons(fileName: String): Future[Done] =
    parse(s"$fileName")(Mappers.toPerson)
      .grouped(chunkSize)
      .mapAsyncUnordered(cores)(persons => Future.traverse(persons)(p => personRepository.save(p).unsafeToFuture()))
      .run()

  def loadEpisodes(fileName: String): Future[Done] =
    parse(s"$fileName")(Mappers.toEpisode)
      .grouped(chunkSize)
      .mapAsyncUnordered(cores)(episodes => Future.traverse(episodes)(e => episodeRepository.save(e).unsafeToFuture()))
      .run()

  def loadRatings(fileName: String): Future[Done] =
    parse(s"$fileName")(Mappers.toRating)
      .grouped(chunkSize)
      .mapAsyncUnordered(cores)(ratings => Future.traverse(ratings)(r => ratingRepository.save(r).unsafeToFuture()))
      .run()

  def loadPrincipals(fileName: String): Future[Done] =
    parse(s"$fileName")(Mappers.toPrincipal)
      .grouped(chunkSize)
      .mapAsyncUnordered(cores)(principals =>
        Future.traverse(principals)(p => principalRepository.save(p).unsafeToFuture()),
      )
      .run()

  def loadTitles(fileName: String): Future[Done] =
    parse(s"$fileName")(Mappers.toTitle)
      .grouped(chunkSize)
      .mapAsyncUnordered(cores)(titles => Future.traverse(titles)(t => titleRepository.save(t).unsafeToFuture()))
      .run()

  def loadData(): Future[Done] = {
    loadTitles(titleFile)
      .flatMap(_ =>
        loadPersons(personFile).flatMap(_ =>
          Future.sequence(
            Seq(
              loadEpisodes(episodeFile),
              loadRatings(ratingFile),
              loadPrincipals(principalFile),
            ),
          ),
        ),
      )
      .map(_ => Done)
  }
}

object DataFeeder {
  val personFile: String = getClass.getResource("/csv/name.basics.csv").getPath
  val episodeFile: String = getClass.getResource("/csv/title.episode.csv").getPath
  val ratingFile: String = getClass.getResource("/csv/title.ratings.csv").getPath
  val principalFile: String = getClass.getResource(s"/csv/title.principals.csv").getPath
  val titleFile: String = getClass.getResource("/csv/title.basics.csv").getPath

  lazy val cores: Int = Runtime.getRuntime.availableProcessors / 2
  val delimiter: Byte = CsvParsing.SemiColon
  val quoteChar: Byte = CsvParsing.Tab
  val chunkSize: Int = 2000
  lazy val flow: Flow[ByteString, List[ByteString], NotUsed] = CsvParsing.lineScanner(delimiter, quoteChar)

}
