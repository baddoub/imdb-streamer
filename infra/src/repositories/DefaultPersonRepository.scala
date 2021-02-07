package repositories

import cats.effect.IO
import domain.Person
import doobie.implicits._

class DefaultPersonRepository(val xa: doobie.Transactor[IO]) extends PersonRepository {
  override def save(person: Person): IO[Int] = {
    println(s"Persisting : $person")
    sql"""INSERT INTO persons (id,
         |primary_name,
         |birth_year,
         |death_year,
         |primary_profession,
         |known_for_titles)
         |VALUES
         |(${person.id},
         |${person.primaryName},
         |${person.birthYear},
         |${person.deathYear},
         |${person.primaryProfession},
         |${person.knownForTitles.mkString(",")})
         |""".stripMargin.update.run.transact(xa)
  }

}
