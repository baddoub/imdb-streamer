package domain

import domain.Title.TitleType

final case class Title(
    id: String,
    titleType: TitleType,
    primaryTitle: String,
    originalTitle: String,
    isAdult: Int,
    startYear: Option[Int],
    endYear: Option[Int],
    runtimeMinutes: Long,
    genres: Seq[String],
)

object Title {

  sealed abstract class TitleType(val name: String)

  object TitleType {

    // Enhancement 1 : Use sealrate
    val all: Seq[TitleType] = Seq(Movie, Video, Short, TvSeries, TvEpisode)

    // Enhancement 2 : Use Either/Try/IO for better error handling
    def from(value: String): Option[TitleType] =
      all.find(_.name == value)

    case object Movie extends TitleType("movie")

    case object Video extends TitleType("video")

    case object Short extends TitleType("short")

    case object TvSeries extends TitleType("tvSeries")

    case object TvEpisode extends TitleType("tvEpisode")

  }
}
