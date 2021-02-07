package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import domain.{Actor, GreatestTvSeries}
import repositories.{PrincipalRepository, TitleRepository}

class MovieService(principalRepository: PrincipalRepository, titleRepository: TitleRepository) {
  def principalsForMovieName(name: String): Source[Actor, NotUsed] =
    Source(principalRepository.principalsOfMovieName(name).unsafeRunSync())

  def tvSeriesWithGreatNumberOfEpisodes(): Source[GreatestTvSeries, NotUsed] =
    Source.single(titleRepository.tvSeriesWithGreatestNumberOfEpisodes().unsafeRunSync())
}
