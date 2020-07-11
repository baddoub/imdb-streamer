package repositories

import com.danielasfregola.randomdatagenerator.RandomDataGenerator
import domain.{Person, Principal, Title}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import repositories.RepoSqlContext.DbConf

class BaseRepoSpec extends AnyFunSpec with RandomDataGenerator with Matchers with BeforeAndAfterEach {
  lazy val dbConf: DbConf =
    DbConf("jdbc:h2:mem:imdb_db;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "postgres", "")
  lazy val db: RepoSqlContext = new RepoSqlContext(dbConf)
  protected val principal: Principal = random[Principal]
  protected val person: Person = random[Person]
  protected val title: Title = random[Title]

  override def beforeEach(): Unit = db.migrate().unsafeRunSync()

  override def afterEach(): Unit = db.dropTables().unsafeRunSync()

}
