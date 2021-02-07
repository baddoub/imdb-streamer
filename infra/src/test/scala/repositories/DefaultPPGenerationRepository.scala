package repositories
import cats.implicits._
class DefaultPPGenerationRepository extends BaseRepoSpec {

  "loadTopPowerPlants" should "load the top power plants." in {
    val limit = 5
    val _ = powerPlants.map(powerPlantRepository.save).sequence.unsafeRunSync()
    val res = powerPlantRepository.loadTopPowerPlants(limit).unsafeRunSync()
    res shouldBe powerPlants.sortBy(_.netGeneration).reverse.take(limit)
  }
}
