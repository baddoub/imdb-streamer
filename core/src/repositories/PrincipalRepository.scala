package repositories

import cats.effect.IO
import domain.Principal

trait PrincipalRepository {
  def save(principal: Principal): IO[Int]

  def principalsOfMovieName(title: String): IO[Seq[Principal]]

  def selectAll(): IO[List[Principal]]
}
