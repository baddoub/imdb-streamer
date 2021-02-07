package utils

import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, StreamConverters}
import akka.util.ByteString
import akka.{Done, NotUsed}
import cats.effect.IO
import domain.{State, StatePPGeneration}
import repositories.{PowerPlantRepository, StatePPGenerationRepository}
import utils.DataFeeder._

import scala.concurrent.{ExecutionContext, Future}

class DataFeeder(
    implicit executionContext: ExecutionContext,
    statePPGenerationRepository: StatePPGenerationRepository[IO],
    powerPlantRepository: PowerPlantRepository[IO],
    actorSystem: ActorSystem,
) {

  private def parse[A](fileName: String)(parser: Map[String, String] => Option[A]): Source[A, Future[IOResult]] = {
    StreamConverters
      .fromInputStream(() => getClass.getResourceAsStream(fileName))
      .via(flow)
      .via(CsvToMap.toMapAsStrings())
      .via(Flow[Map[String, String]].map(parser))
      .collect {
        case Some(v) => v
      }
  }

  def loadStatesRef(fileName: String): Future[List[State]] = {
    parse(fileName)(Mappers.toState(_))
      .toMat(Sink.collection)(Keep.right)
      .run()
      .map(_.toList)
  }

  def loadPowerPlantsData(fileName: String): Future[Done] = {
    parse(fileName)(pp => Mappers.toPowerPlant(pp))
      .grouped(200)
      .mapAsyncUnordered(cores)(pps => Future.traverse(pps)(p => powerPlantRepository.save(p).unsafeToFuture()))
      .run()
  }
  def loadStatesData(fileName: String, statesRef: List[State]): Future[Done] = {
    parse(fileName)(pp => Mappers.toStatePowerPlant(pp, statesRef))
      .groupBy(51, p => p.state)
      .reduce(_ + _)
      .mergeSubstreams
      .fold((Seq.empty[StatePPGeneration], 0L))((acc, ele) => {
        (acc._1 :+ ele, acc._2 + ele.annualNetGeneration)
      })
      .map {
        case (seq, total) =>
          seq.map(pp => pp.withPercentage(MathUtils.percent(pp.annualNetGeneration, total)))
      }
      .mapAsyncUnordered(2)(statePPGenerations =>
        Future.traverse(statePPGenerations)(s => statePPGenerationRepository.save(s).unsafeToFuture()),
      )
      .run()
  }

}

object DataFeeder {
  lazy val cores: Int = Runtime.getRuntime.availableProcessors / 2
  val delimiter: Byte = CsvParsing.SemiColon
  val quoteChar: Byte = CsvParsing.DoubleQuote
  lazy val flow: Flow[ByteString, List[ByteString], NotUsed] = CsvParsing.lineScanner(delimiter, quoteChar)
}
