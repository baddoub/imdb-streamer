package repositories

import cats.effect.IO
import domain.Rating
import doobie.implicits._

class DefaultRatingRepository(val xa: doobie.Transactor[IO]) extends RatingRepository {
  override def save(rating: Rating): IO[Int] = {
    println(s"Persisting  : $rating")
    sql"""INSERT INTO ratings (
         |title_id,
         |average_rating,
         |num_votes) 
         |VALUES (
         |${rating.titleId}, 
         |${rating.averageRating},
         | ${rating.numVotes})
         |""".stripMargin.update.run.transact(xa)
  }
}
