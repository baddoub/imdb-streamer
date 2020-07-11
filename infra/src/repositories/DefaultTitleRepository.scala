package repositories

import cats.effect.IO
import domain.Title.TitleType
import domain.Title.TitleType.TvSeries
import domain.{GreatestTvSeries, Title}
import doobie.implicits._
import doobie.util.Meta

class DefaultTitleRepository(val xa: doobie.Transactor[IO]) extends TitleRepository {
  override def save(title: Title): IO[Int] = {
    println(s"Persisting : $title")
    sql"""INSERT INTO titles
         |(id, title_type, primary_title, original_title, is_adult, start_year, end_year, runtime_minutes, genres)
         |VALUES (${title.id},
         |${title.titleType.name},
         |${title.primaryTitle},
         |${title.originalTitle},
         |${title.isAdult},
         |${title.startYear},
         |${title.endYear},
         |${title.runtimeMinutes},
         |${title.genres.mkString(",")})
         |""".stripMargin.update.run.transact(xa)
  }

  override def tvSeriesWithGreatestNumberOfEpisodes(): IO[GreatestTvSeries] = {
    sql"""SELECT t.id, t.primary_title, COUNT(*) as totalCount
         |FROM  episodes e
         |       INNER JOIN titles t
         |            ON t.id = e.parent_id
         |WHERE   t.title_type = ${TvSeries.name}
         |GROUP   BY t.id, t.primary_title
         |ORDER BY totalCount DESC
         |LIMIT 1
         |""".stripMargin
      .query[GreatestTvSeries]
      .unique
      .transact(xa)
  }
}
object DefaultTitleRepository {
  implicit val titleTypeMeta: Meta[Title.TitleType] = Meta[String].timap(TitleType.from(_).get)(_.name)
}
