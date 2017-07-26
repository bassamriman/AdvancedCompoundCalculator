import java.math.BigDecimal

interface IRatio<N : Unit, D : Unit>{
    val numerator : Amount<N>
    val denominator : Amount<D>
    fun numerator() = numerator
    fun denominator() = denominator
    fun netAmount() : BigDecimal = numerator.quantity / denominator.quantity

    interface Unitless : IRatio<Nothing, Nothing>{
        fun toAmount() : Amount.Unitless = Quantity.unitless(netAmount())
        interface Zero : Unitless{
            override fun netAmount() : BigDecimal = 0.bd
        }
        interface Percent : Unitless{
            override fun denominator(): Amount<Nothing> = Quantity.unitless(100.bd)
            override fun netAmount() : BigDecimal = numerator.quantity / 100.bd
        }
    }
    interface Rate< N : Unit, D : Unit> : IRatio<N,D>{
        interface UnitDenominator<N : Unit, D : Unit> : Rate<N,D>{
            override fun netAmount() : BigDecimal = numerator.quantity
        }
        interface UnitNumerator<N : Unit, D : Unit> : Rate<N,D>
        interface UnitRate<N : Unit, D : Unit> : UnitDenominator<N,D>, UnitNumerator<N,D>, Rate<N,D>
    }

    operator fun plus(other: IRatio<N,D>) : IRatio<N,D>
    operator fun minus(other: IRatio<N,D>) : IRatio<N,D>
    operator fun times(other: IRatio<D,N>) : IRatio.Unitless
    operator fun div(other: IRatio<N,D>) : IRatio.Unitless

    operator fun times(other: BigDecimal) : IRatio<N,D>
    operator fun div(other: BigDecimal) : IRatio<N,D>

    operator fun times(other: D) : Amount<N>

    infix fun X(other: IRatio<D,N>) : IRatio.Unitless
    infix fun <X : Unit> Xdec(other: IRatio<D,X>) : IRatio<N,X>
    infix fun <X : Unit> Xinc(other: IRatio<X,N>) : IRatio<X,D>
    fun  <Y: Unit, X : Unit, W: Unit, Z : Unit> multiplyWithUnitChange(other: IRatio<Y,X>, numeratorUnit: W, denominatorUnit : Z) : IRatio<W, Z>

    fun inverse() : IRatio<D,N>
}
sealed class Ratio< N : Unit, D : Unit>(override val numerator: Amount<N>, override val denominator: Amount<D>) : IRatio<N,D> {
    override fun times(other: BigDecimal): IRatio<N, D> = Ratio.of(this.numerator * other, this.denominator)

    override fun div(other: BigDecimal): IRatio<N, D> = Ratio.of(this.numerator, this.denominator*other)

    override fun times(other: D): Amount<N> = Quantity.of(this.netAmount(), this.numerator().unit)

    final override operator fun plus(other: IRatio<N,D>) : IRatio<N,D> = sumMinus(this, other)
    final override operator fun minus(other: IRatio<N,D>) : IRatio<N,D> = sumMinus(this, other)
    final override operator fun times(other: IRatio<D,N>) : IRatio.Unitless = Ratio.unitless(this.numerator.toUnitless() * other.numerator.toUnitless(), denominator)
    final override infix fun X(other: IRatio<D,N>) : IRatio.Unitless = times(other)
    final override infix fun <X : Unit> Xdec(other: IRatio<D,X>) : IRatio<N,X> = multiplyWithUnitChange(other, this.numerator.unit, other.denominator.unit)
    final override infix fun <X : Unit> Xinc(other: IRatio<X,N>) : IRatio<X,D> = multiplyWithUnitChange(other, other.numerator.unit, this.denominator.unit)
    final override operator fun div(other: IRatio<N,D>) :  IRatio.Unitless = times(other.inverse())



    final override fun  <Y: Unit, X : Unit, W: Unit, Z : Unit> multiplyWithUnitChange(other: IRatio<Y,X>, numeratorUnit: W, denominatorUnit : Z) : IRatio<W, Z> = Ratio.of((this.numerator.toUnitless() * other.numerator.toUnitless()).changeUnit(numeratorUnit), (this.denominator.toUnitless() * other.denominator.toUnitless()).changeUnit(denominatorUnit))

    sealed class Unitless (override val numerator : Amount<Nothing>, override val denominator : Amount<Nothing>) : Ratio<Nothing, Nothing>(numerator, denominator), IRatio.Unitless{
        data class Of internal constructor(val numeratorQuantity : BigDecimal, val denominatorQuantity : BigDecimal) : Unitless(Quantity.of(numeratorQuantity, Nothing) , Quantity.of(denominatorQuantity, Nothing))
        data class Percent internal constructor(val numeratorQuantity : BigDecimal) : Unitless(Quantity.of(numeratorQuantity, Nothing), Quantity.of(100.bd, Nothing)), IRatio.Unitless.Percent
        object Zero : Unitless(Quantity.zero(), Quantity.zero())
    }
    open class Rate < N : Unit, D : Unit> (override val numerator : Amount<N>, override val denominator : Amount<D>) : Ratio<N,D>(numerator, denominator){
        data class Of< N : Unit,  D : Unit> internal constructor(override val numerator : Amount<N>, override val denominator : Amount<D>) : Rate<N,D>(numerator, denominator)
        data class WithUnitDenominator< X : Unit, D : Unit> internal constructor(override val numerator : Amount<X>, val denominatorUnit : D) : Rate<X,D>(numerator, Quantity.unit(denominatorUnit)), IRatio.Rate.UnitDenominator<X,D>
        data class WithUnitNumerator< X : Unit, D : Unit> internal constructor( val numeratorUnit : X, override val denominator : Amount<D>) : Rate<X,D>(Quantity.unit(numeratorUnit), denominator), IRatio.Rate.UnitNumerator<X,D>
        data class OfUnits< X : Unit, D : Unit> internal constructor( val numeratorUnit : X, val denominatorUnit : D) : Rate<X,D>(Quantity.unit(numeratorUnit), Quantity.unit(denominatorUnit)), IRatio.Rate.UnitRate<X,D>, IRatio.Rate.UnitNumerator<X,D>, IRatio.Rate.UnitDenominator<X,D>
    }

    override fun inverse() = Ratio.of(this.denominator, this.numerator)
    fun result() =  numerator.quantity.divide(denominator.quantity)
    fun isUnitless() = Ratio.unitlessPredicate(numerator, denominator)
    fun hasUnitOnlyDenominator() = denominator is Amount.UnitOnly
    fun hasUnitOnlyNumerator() = numerator is Amount.UnitOnly
    fun isUnitRate() = numerator is Amount.UnitOnly && denominator is Amount.UnitOnly
    fun unitDenominator() : IRatio<N, D> = Ratio.of(
            numerator = Quantity.of(this.numerator.quantity.multiply(this.denominator.quantity), this.numerator.unit),
            denominator = Quantity.unit(this.denominator.unit))

    fun unitNumerator() : IRatio<N, D> = Ratio.of(
            numerator = Quantity.unit(this.numerator.unit),
            denominator =  Quantity.of(this.denominator.quantity.multiply(this.numerator.quantity), this.denominator.unit))


    companion object{
        fun <N : Unit, D : Unit> of(numerator: Amount<N>, denominator: Amount<D>) : IRatio<N, D> = when {
            numerator is Amount.Unitless && denominator is Amount.Unitless -> Ratio.Unitless.Of(numerator.quantity, denominator.quantity) as Ratio<N,D>
            else ->  Ratio.Rate.Of(numerator, denominator)
        }

        fun of(numerator: BigDecimal, denominator: BigDecimal) : IRatio.Unitless = Ratio.Unitless.Of(numerator, denominator)

        fun  <D : Unit> of(numerator: BigDecimal, denominator: Amount<D>) :IRatio.Rate<Nothing, D> = Ratio.of(numerator, denominator)

        fun percent(numerator: BigDecimal) :IRatio.Unitless.Percent = Ratio.Unitless.Percent(numerator)

        fun <N : Unit, D : Unit>  ofUnits(numerator: N, denominator: D) : IRatio.Rate.UnitRate<N, D> = when {
            numerator is Nothing && denominator is Nothing -> throw IllegalArgumentException()
            else -> Ratio.Rate.OfUnits(numerator, denominator)
        }

        fun <N : Unit, D : Unit>  withUnitDenominator(numerator: Amount<N>, denominator: D) : IRatio.Rate.UnitDenominator<N, D> = Ratio.Rate.WithUnitDenominator(numerator, denominator)
        fun <N : Unit, D : Unit>  withUnitNumerator(numerator: N, denominator: Amount<D>) : IRatio.Rate.UnitNumerator<N, D> = Ratio.Rate.WithUnitNumerator(numerator, denominator)

        fun <N : Unit, D : Unit> unitless(numerator: Amount<N>, denominator: Amount<D>) : IRatio.Unitless = Ratio.Unitless.Of(numerator.quantity, denominator.quantity)
        fun <N : Unit, D : Unit> unitlessPredicate (numerator: Amount<N>, denominator: Amount<D>) = numerator is Amount.Unitless && denominator is Amount.Unitless
        fun <N : Unit, D : Unit> unitlessRatioPredicate (numerator: Amount<N>, denominator: Amount<N>) = unitlessPredicate(numerator, denominator)

        private fun  <N : Unit, D : Unit>  sumMinus(ratioA:  IRatio<N,D>, ratioB:  IRatio<N,D>) : IRatio<N,D>{
            if (ratioA.denominator == ratioB.denominator){
                return Ratio.of(numerator = ratioA.numerator + ratioB.numerator, denominator = ratioA.denominator )
            }else{
                return Ratio.of(numerator = Quantity.of(ratioA.numerator.quantity*ratioB.denominator.quantity + ratioB.numerator.quantity * ratioA.numerator.quantity, ratioA.numerator.unit),
                        denominator = Quantity.of(ratioA.denominator.quantity * ratioB.denominator.quantity, ratioA.denominator.unit))
            }
        }
    }
}
