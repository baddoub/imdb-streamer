package api.dto

import domain.StatePPGeneration.WithPercentage

final case class StatePPGeneration(state: String, netGeneration: Long, percentage: Double)
object StatePPGeneration {
  def apply(withPercentage: WithPercentage) =
    new StatePPGeneration(
      withPercentage.stateNetGeneration.state,
      withPercentage.stateNetGeneration.annualNetGeneration,
      withPercentage.percentage,
    )
}
