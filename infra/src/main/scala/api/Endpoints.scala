package api

import api.dto.StatePPGeneration
import domain.PowerPlant
import sttp.tapir.{Endpoint, endpoint, _}
import sttp.tapir.json.circe.jsonBody
import io.circe.generic.semiauto.deriveCodec

object Endpoints {
  implicit val statePPGenerationCodec: io.circe.Codec[StatePPGeneration] =
    deriveCodec[StatePPGeneration]
  implicit val powerPlantCodec: io.circe.Codec[PowerPlant] =
    deriveCodec[PowerPlant]

  val loadTopStatesPPGeneration: Endpoint[Int, Unit, List[StatePPGeneration], Nothing] = endpoint
    .name("Fetch n states with top power plant net generation")
    .get
    .in("states" / "search" / query[Int]("limit").description("Limit param represents the number of states to show."))
    .description("Api for fetching N state with top power plant net generation.")
    .out(jsonBody[List[StatePPGeneration]])

  val loadTopPowerPlants: Endpoint[Int, Unit, List[PowerPlant], Nothing] = endpoint
    .name("Fetch n top power plants")
    .get
    .in(
      "power-plants" / "search" / query[Int]("limit")
        .description("Limit param represents the number of power plants to show."),
    )
    .description("Api for fetching N top power plants net generation.")
    .out(jsonBody[List[PowerPlant]])
}
