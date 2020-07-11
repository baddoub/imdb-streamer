package repositories

import cats.effect.IO
import domain.{Actor, Principal}

trait PrincipalRepository {
  def save(principal: Principal): IO[Int]

  def principalsOfMovieName(title: String): IO[List[Actor]]

  def selectAll(): IO[List[Principal]]
}
