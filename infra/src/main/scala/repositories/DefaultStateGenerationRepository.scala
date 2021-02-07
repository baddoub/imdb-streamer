package repositories

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import domain.StatePPGeneration
import doobie.implicits._
import doobie.util.transactor.Transactor

class DefaultStateGenerationRepository(xa: Transactor[IO]) extends StatePPGenerationRepository[IO] with StrictLogging {

  override def loadStatesWithTopPPGeneration(limit: Int): IO[List[StatePPGeneration.WithPercentage]] = {
    logger.info(s"Loading $limit top power plants!")
    sql"SELECT state, annual_net_generation, percentage FROM states_pp_generation ORDER BY annual_net_generation DESC LIMIT $limit"
      .query[StatePPGeneration.WithPercentage]
      .to[List]
      .transact(xa)
  }

  override def save(statePPGenerationWithPercent: StatePPGeneration.WithPercentage): IO[Int] = {
    logger.info(s"Persisting state power plant net generation $statePPGenerationWithPercent")
    sql"""INSERT INTO states_pp_generation (state, annual_net_generation, percentage)
         |VALUES (${statePPGenerationWithPercent.stateNetGeneration.state},
         |${statePPGenerationWithPercent.stateNetGeneration.annualNetGeneration},
         |${statePPGenerationWithPercent.percentage})
         |""".stripMargin.update.run
      .transact(xa)
  }
}
