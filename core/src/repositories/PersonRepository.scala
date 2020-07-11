package repositories

import cats.effect.IO
import domain.Person

trait PersonRepository {

  def save(person: Person): IO[Int]
}
