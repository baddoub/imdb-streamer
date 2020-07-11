package repositories

import cats.effect.IO
import domain.Principal
import domain.Title.TitleType.Movie
import doobie.implicits._

class DefaultPrincipalRepository(val xa: doobie.Transactor[IO]) extends PrincipalRepository {
  override def save(principal: Principal): IO[Int] = {
    println(s"Persisting : $principal")
    sql"""INSERT INTO principals (title_id, ordering, person_id, category, job, characters)
         |VALUES (
         |${principal.titleId},
         |${principal.ordering},
         |${principal.personId},
         |${principal.category},
         |${principal.job},
         |${principal.characters})
         |""".stripMargin.update.run.transact(xa)
  }

  override def principalsOfMovieName(title: String): IO[List[Principal]] =
    sql"""SELECT p.title_id, p.ordering, p.person_id, p.category, p.job, p.characters FROM titles t
         |INNER JOIN principals p ON p.title_id = t.id
         |WHERE t.primary_title = $title and t.title_type = ${Movie.name}
         |""".stripMargin
      .query[Principal]
      .stream
      .compile
      .toList
      .transact(xa)

  override def selectAll(): IO[List[Principal]] =
    sql"""Select * FROM principals
         |""".stripMargin
      .query[Principal]
      .stream
      .compile
      .toList
      .transact(xa)
}
