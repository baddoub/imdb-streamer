package repositories

trait Repos {

  val titleRepository: TitleRepository
  val personRepository: PersonRepository
  val ratingRepository: RatingRepository
  val episodeRepository: EpisodeRepository
  val principalRepository: PrincipalRepository

}
