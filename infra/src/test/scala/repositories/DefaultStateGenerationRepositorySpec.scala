package repositories

import cats.implicits._
import org.scalatest.BeforeAndAfterEach
class DefaultStateGenerationRepositorySpec extends BaseRepoSpec with BeforeAndAfterEach {

  "loadStatesWithTopPPGeneration" should "load n states with top pp generation" in {
    val limit = 8
    val _ = statesPPGeneration.map(statePPGenerationRepository.save).sequence.unsafeRunSync()
    val res = statePPGenerationRepository.loadStatesWithTopPPGeneration(limit).unsafeRunSync()
    res shouldBe statesPPGeneration.sortBy(_.stateNetGeneration.annualNetGeneration).reverse.take(8)
  }
}
