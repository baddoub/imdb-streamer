package repositories

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import domain.PowerPlant
import doobie.Transactor
import doobie.implicits._

class DefaultPowerPlantRepository(xa: Transactor[IO]) extends PowerPlantRepository[IO] with StrictLogging {
  override def save(pp: PowerPlant): IO[Int] = {
    logger.info(s"Persisting power plant $pp")
    sql"""INSERT INTO power_plants(name, longitude, latitude, annual_net_generation) 
         |VALUES (${pp.name}, ${pp.longitude}, ${pp.latitude}, ${pp.netGeneration})""".stripMargin.update.run
      .transact(xa)
  }

  override def loadTopPowerPlants(limit: Int): IO[List[PowerPlant]] =
    sql"""SELECT name, longitude, latitude, annual_net_generation FROM power_plants 
         |ORDER BY annual_net_generation DESC LIMIT $limit""".stripMargin
      .query[PowerPlant]
      .to[List]
      .transact(xa)
}
