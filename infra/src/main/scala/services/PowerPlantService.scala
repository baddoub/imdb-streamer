package services

import domain.PowerPlant
import repositories.PowerPlantRepository

object PowerPlantService {

  def loadTopPowerPlants[F[_]: PowerPlantRepository](limit: Int): F[List[PowerPlant]] =
    PowerPlantRepository[F].loadTopPowerPlants(limit)
}
