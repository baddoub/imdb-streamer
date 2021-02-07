package utils

object MathUtils {
  def percent(v: Long, total: Long): Double =
    BigDecimal(v * 100 / total.toDouble).setScale(2, BigDecimal.RoundingMode.HALF_EVEN).toDouble
}
