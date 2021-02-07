package repositories

import domain.StatePPGeneration

trait StatePPGenerationRepository[F[_]] {
  def loadStatesWithTopPPGeneration(limit: Int): F[List[StatePPGeneration.WithPercentage]]
  def save(statePPGeneration: StatePPGeneration.WithPercentage): F[Int]
}

object StatePPGenerationRepository {
  def apply[F[_]](implicit StatePPGRepo: StatePPGenerationRepository[F]): StatePPGenerationRepository[F] = StatePPGRepo
}
