package domain

final case class Person(
    id: String,
    primaryName: String,
    birthYear: Option[Int],
    deathYear: Option[Int],
    primaryProfession: String,
    knownForTitles: Seq[String],
)
