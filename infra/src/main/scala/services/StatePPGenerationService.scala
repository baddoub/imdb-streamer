package services

import domain.StatePPGeneration
import repositories.StatePPGenerationRepository

object StatePPGenerationService {

  def loadStatesWithTopPPGeneration[F[_]: StatePPGenerationRepository](
      limit: Int,
  ): F[List[StatePPGeneration.WithPercentage]] = {
    StatePPGenerationRepository[F].loadStatesWithTopPPGeneration(limit)
  }

}
