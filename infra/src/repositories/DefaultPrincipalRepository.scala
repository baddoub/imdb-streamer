package repositories

import cats.effect.IO
import domain.Title.TitleType.{Movie, TvMovie}
import domain.{Actor, Principal}
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

  override def principalsOfMovieName(title: String): IO[List[Actor]] = {
    sql"""SELECT ps.primary_name, ps.primary_profession, p.job, p.characters FROM titles t
         |INNER JOIN principals p ON p.title_id = t.id
         |INNER JOIN persons ps ON ps.id = p.person_id
         |WHERE t.primary_title = $title and (t.title_type = ${Movie.name} OR t.title_type = ${TvMovie.name})
         |""".stripMargin
      .query[Actor]
      .to[List]
      .transact(xa)
  }

  override def selectAll(): IO[List[Principal]] =
    sql"""Select * FROM principals
         |""".stripMargin
      .query[Principal]
      .to[List]
      .transact(xa)
}
