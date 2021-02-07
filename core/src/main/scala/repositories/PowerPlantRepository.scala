package repositories

import domain.PowerPlant

trait PowerPlantRepository[F[_]] {
  def save(pp: PowerPlant): F[Int]

  def loadTopPowerPlants(limit: Int): F[List[PowerPlant]]
}

object PowerPlantRepository {
  def apply[F[_]](implicit PowerPlantRepo: PowerPlantRepository[F]): PowerPlantRepository[F] = PowerPlantRepo
}
