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
    runtimeMinutes: Option[Long],
    genres: Seq[String],
)

object Title {

  sealed abstract class TitleType(val name: String)

  object TitleType {

    // Enhancement 1 : Use sealrate
    val all: Seq[TitleType] = Seq(Movie, Video, Short, TvSeries, TvEpisode, TvMovie, TvShort, TvMiniSeries, TvSpecial)

    // Enhancement 2 : Use Either/Try/IO for better error handling
    def from(value: String): Option[TitleType] =
      all.find(_.name == value)

    final case object Movie extends TitleType("movie")

    final case object TvMovie extends TitleType("tvMovie")

    final case object Video extends TitleType("video")

    final case object Short extends TitleType("short")

    final case object TvShort extends TitleType("tvShort")

    final case object TvSeries extends TitleType("tvSeries")

    final case object TvMiniSeries extends TitleType("tvMiniSeries")

    final case object TvEpisode extends TitleType("tvEpisode")

    final case object TvSpecial extends TitleType("tvSpecial")

  }
}
