package repositories

import cats.effect.IO
import domain.Rating

trait RatingRepository {
  def save(rating: Rating): IO[Int]
}
