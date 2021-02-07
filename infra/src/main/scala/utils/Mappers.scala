package utils

import domain.{PowerPlant, State, StatePPGeneration}

import scala.util.Try

object Mappers {

  val stateCol = "PSTATABB"
  val annualGenerationCol = "PLNGENAN"
  val longitudeCol = "LON"
  val latitudeCol = "LAT"
  val nameCol = "PNAME"
  def toPowerPlant(in: Map[String, String]): Option[PowerPlant] = {
    for {
      log <- asDouble(in, longitudeCol)
      lat <- asDouble(in, latitudeCol)
      name <- in.get(nameCol)
      gen <- asLong(in, annualGenerationCol)
    } yield PowerPlant(name, log, lat, gen)
  }

  private def asDouble(in: Map[String, String], col: String): Option[Double] = {
    in.get(col).flatMap(d => Try(d.replace(",", ".").toDouble).toOption)
  }
  private def asLong(in: Map[String, String], col: String): Option[Long] = {
    in.get(col)
      .flatMap(s => Try(s.replace(" ", "").toLong).toOption.orElse(Some(0L)))
  }
  def toState(in: Map[String, String]): Option[State] =
    for {
      code <- in.get("code")
      name <- in.get("name")
    } yield State(code, name)

  def toStatePowerPlant(in: Map[String, String], stateRef: Seq[State]): Option[StatePPGeneration] = {
    for {
      state <- in
        .get(stateCol)
        .flatMap(code => stateRef.find(_.code == code))
        .map(_.name)
      annualGeneration <- asLong(in, annualGenerationCol)
    } yield StatePPGeneration(state, annualGeneration)
  }
}
