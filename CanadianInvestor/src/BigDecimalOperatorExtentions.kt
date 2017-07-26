import java.math.BigDecimal

operator fun <U : Unit> BigDecimal.times(unit : U) : Amount<U> = Quantity.of(this, unit)
operator fun <U : Unit> BigDecimal.div(unit : U) : IRatio.Rate.UnitDenominator<Nothing, U> = Ratio.withUnitDenominator(Quantity.unitless(this), unit)

operator fun <U : Unit> BigDecimal.times(amount : Amount<U>) : Amount<U> = amount * this
operator fun <U : Unit> BigDecimal.div(amount : Amount<U>) : IRatio.Rate<Nothing, U> = Ratio.of(this, amount)

operator fun <N : Unit, D : Unit> BigDecimal.times(ratio : IRatio<N, D>) : IRatio<N, D> = ratio * this
operator fun <N : Unit, D : Unit> BigDecimal.div(ratio : IRatio<N, D>) : IRatio<D, N> = Ratio.of(this * ratio.denominator, ratio.numerator)