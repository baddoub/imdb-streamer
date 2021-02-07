package repositories

import com.danielasfregola.randomdatagenerator.RandomDataGenerator
import domain.{PowerPlant, StatePPGeneration}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repositories.RepoSqlContext.DbConf
import org.scalatest.funspec.AnyFunSpec

class BaseRepoSpec extends AnyFlatSpec with RandomDataGenerator with Matchers with BeforeAndAfterEach {
  lazy val dbConf: DbConf =
    DbConf("jdbc:h2:mem:imdb_db;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "postgres", "")
  lazy val db: RepoSqlContext = new RepoSqlContext(dbConf)

  protected val powerPlants = random[PowerPlant](10).distinctBy(_.name).filter(_.name.nonEmpty).toList
  protected val statesPPGeneration =
    random[StatePPGeneration.WithPercentage](10).distinctBy(_.stateNetGeneration.state).toList

  protected val statePPGenerationRepository = new DefaultStateGenerationRepository(db.xa)
  protected val powerPlantRepository = new DefaultPowerPlantRepository(db.xa)

  override def beforeEach(): Unit = db.migrate().unsafeRunSync()
  override def afterEach(): Unit = db.dropTables().unsafeRunSync()

}
