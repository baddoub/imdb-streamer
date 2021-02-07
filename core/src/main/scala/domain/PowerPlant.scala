package domain

// TODO use refined types for longitude and latitude
final case class PowerPlant(name: String, longitude: Double, latitude: Double, netGeneration: Long)
