package repositories

import cats.effect.IO
import domain.{GreatestTvSeries, Title}

trait TitleRepository {
  def save(title: Title): IO[Int]

  def tvSeriesWithGreatestNumberOfEpisodes(): IO[GreatestTvSeries]

}
