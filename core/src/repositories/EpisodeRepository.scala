package repositories

import cats.effect.IO
import domain.Episode

trait EpisodeRepository {

  def save(episode: Episode): IO[Int]
}
