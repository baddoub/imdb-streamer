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
    sql"""SELECT t.primary_title, COUNT(*) as episodes  FROM episodes e
         |INNER JOIN titles t ON e.parentId = t.id
         |WHERE t.title_type = ${TvSeries.name}
         |GROUP BY (t.primary_title)
         |LIMIT 1
         |""".stripMargin
      .query[GreatestTvSeries]
      .unique
      .transact(xa)
  }
}
object DefaultTitleRepository {
  implicit val titleTypedeMeta: Meta[Title.TitleType] = Meta[String].timap(TitleType.from(_).get)(_.name)
}
