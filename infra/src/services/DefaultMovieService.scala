package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import domain.{GreatestTvSeries, Principal}
import repositories.{PrincipalRepository, TitleRepository}

class DefaultMovieService(principalRepository: PrincipalRepository, titleRepository: TitleRepository) {
  def principalsForMovieName(name: String): Source[Principal, NotUsed] =
    Source(principalRepository.principalsOfMovieName(name).unsafeRunSync())
  def selectAll: Source[Principal, NotUsed] = Source(principalRepository.selectAll().unsafeRunSync())

  def tvSeriesWithGreatNumberOfEpisodes(): Source[GreatestTvSeries, NotUsed] =
    Source.future(titleRepository.tvSeriesWithGreatestNumberOfEpisodes().unsafeToFuture())
}
