package utils

import domain.Title.TitleType
import domain._

import scala.util.Try
import scala.util.matching.Regex

object Mappers {
  val separator: String = ","
  val regex: Regex = "^nm(\\w){2}(\\w)*".r
  val maxId: Int = 68544
  def toPerson(in: Map[String, String]): Option[Person] = {
    for {
      id <- in.get("nconst")
      birthYear = in.get("birthYear").flatMap(v => Try(v.toInt).toOption)
      primaryProfession <- in.get("primaryProfession")
      deathYear = in.get("deathYear").flatMap(v => Try(v.toInt).toOption)
      knownForTitles <- in.get("knownForTitles").map(_.split(separator))
      primaryName <- in.get("primaryName")
    } yield Person(id, primaryName, birthYear, deathYear, primaryProfession, knownForTitles)
  }

  def toEpisode(in: Map[String, String]): Option[Episode] = {
    for {
      id <- in.get("tconst")
      parentId <- in.get("parentTconst")
      seasonNumber <- in.get("seasonNumber").flatMap(v => Try(v.toInt).toOption)
      episodeNumber <- in.get("episodeNumber").flatMap(v => Try(v.toInt).toOption)
    } yield Episode(id, parentId, seasonNumber, episodeNumber)
  }

  def toRating(in: Map[String, String]): Option[Rating] = {
    for {
      id <- in.get("tconst")
      averageRating <- in.get("averageDouble").flatMap(v => Try(v.toDouble).toOption)
      numVotes <- in.get("numVotes").flatMap(v => Try(v.toInt).toOption)
    } yield Rating(id, averageRating, numVotes)
  }
  def toPrincipal(in: Map[String, String]): Option[Principal] = {
    for {
      id <- in.get("tconst")
      ordering <- in.get("ordering").flatMap(v => Try(v.toInt).toOption)
      personId <- in.get("nconst").withFilter { // little hack to allow only existing person ids.
        case regex(id) => id.toInt < maxId
        case _         => false
      }
      category <- in.get("category")
      job <- in.get("job")
      characters <- in.get("characters")
    } yield Principal(id, ordering, personId, category, job, characters)
  }

  def toTitle(in: Map[String, String]): Option[Title] = {
    for {
      id <- in.get("tconst")
      titleType <- in.get("titleType").flatMap(TitleType.from)
      primaryTitle <- in.get("primaryTitle")
      originalTitle <- in.get("originalTitle")
      isAdult <- in.get("isAdult").flatMap(v => Try(v.toInt).toOption)
      startYear = in.get("startYear").flatMap(v => Try(v.toInt).toOption)
      endYear = in.get("endYear").flatMap(v => Try(v.toInt).toOption)
      runtimeMinutes <- in.get("runtimeMinutes").flatMap(v => Try(v.toLong).toOption)
      genres <- in.get("genres").map(_.split(separator))
    } yield Title(id, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres)
  }
}
