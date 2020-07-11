package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import domain.{GreatestTvSeries, Principal}
import repositories.{PrincipalRepository, TitleRepository}

class DefaultMovieService(principalRepository: PrincipalRepository, titleRepository: TitleRepository) {
  def principalsForMovieName(name: String): Source[Seq[Principal], NotUsed] =
    Source.single(principalRepository.principalsOfMovieName(name).unsafeRunSync())

  def tvSeriesWithGreatNumberOfEpisodes(): Source[GreatestTvSeries, NotUsed] =
    Source.single(titleRepository.tvSeriesWithGreatestNumberOfEpisodes().unsafeRunSync())
}
