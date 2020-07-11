package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import domain.{Principal, Title}
import repositories.PrincipalRepository

class DefaultMovieService(principalRepository: PrincipalRepository) {
  def principalsOfMovieName(name: String): Source[Principal, NotUsed] =
    Source(principalRepository.principalsOfMovieName(name).unsafeRunSync())
  def selectAll: Source[Principal, NotUsed] = Source(principalRepository.selectAll().unsafeRunSync())

  def tvSeriesWithGreatNumberOfEpisodes(): Source[Seq[Title], _] = ???
}
