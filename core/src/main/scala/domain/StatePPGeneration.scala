package domain

import domain.StatePPGeneration.WithPercentage

final case class StatePPGeneration(state: String, annualNetGeneration: Long) {
  def +(statePP: StatePPGeneration) =
    StatePPGeneration(state, statePP.annualNetGeneration + annualNetGeneration) // TODO enhance this method to accept only statePPG with same code
  def withPercentage(p: Double): WithPercentage = WithPercentage(this, p)
}
object StatePPGeneration {
  final case class WithPercentage(stateNetGeneration: StatePPGeneration, percentage: Double)
}
