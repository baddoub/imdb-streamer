package repositories

import cats.effect.IO
import domain.Episode

trait EpisodeRepository {
  def selectAll: IO[Seq[Episode]]
  def save(episode: Episode): IO[Int]
}
