package repositories

import cats.effect.IO
import domain.Episode
import doobie.implicits._

class DefaultEpisodeRepository(val xa: doobie.Transactor[IO]) extends EpisodeRepository {
  override def save(episode: Episode): IO[Int] = {
    println(s"Persisting : $episode")
    sql"""INSERT INTO episodes (id, parent_id, season_number, episode_number)
         |VALUES (${episode.id},
         |${episode.parentId},
         |${episode.seasonNumber},
         |${episode.episodeNumber})
         |""".stripMargin.update.run.transact(xa)
  }

  override def selectAll: IO[Seq[Episode]] =
    sql"""Select * FROM episodes
         |""".stripMargin
      .query[Episode]
      .to[List]
      .transact(xa)
}
