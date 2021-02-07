package api

import cats.effect.IO
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import repositories.{PowerPlantRepository, StatePPGenerationRepository}
import api.Endpoints._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{pathSingleSlash, redirect}
import akka.http.scaladsl.server.{Directives, Route}
import api.dto.StatePPGeneration
import cats.Functor
import services.{PowerPlantService, StatePPGenerationService}
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import sttp.tapir.swagger.akkahttp.SwaggerAkka
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import cats.implicits.{catsSyntaxEitherId, catsSyntaxApplicativeId, _}
object HttpServer {

  def routes()(
      implicit
      statePPGenerateRepository: StatePPGenerationRepository[IO],
      powerPlantRepository: PowerPlantRepository[IO],
  ): Route = {

    val documentedApis = Seq(loadTopStatesPPGeneration, loadTopPowerPlants)
    val redirectToDocs = pathSingleSlash(redirect("/docs", StatusCodes.PermanentRedirect))
    val docs = new SwaggerAkka(documentedApis.toOpenAPI("Power plants net generation Public API", "1.0").toYaml).routes

    cors() {
      Directives.concat(
        docs,
        redirectToDocs,
        loadTopStatesPPGeneration.toRoute(limit =>
          Functor[IO]
            .compose[List]
            .map(
              StatePPGenerationService
                .loadStatesWithTopPPGeneration[IO](limit),
            )(StatePPGeneration(_))
            .map(_.asRight[Unit]) // TODO better error handling
            .unsafeToFuture(),
        ),
        loadTopPowerPlants.toRoute(limit =>
          PowerPlantService.loadTopPowerPlants[IO](limit).map(_.asRight[Unit]).unsafeToFuture(),
        ),
      )
    }
  }

}
