import java.math.BigDecimal

interface Amount<U : Unit>{
    val quantity : BigDecimal
    val unit : U
    interface Unsigned<U: Unit> : Amount<U> ,Sign.Unsigned{
        interface Zero<U: Unit> : Unsigned<U>, Sign.Unsigned.Zero
    }
    interface Signed<U: Unit>: Amount<U>, Sign.Signed {
        interface Positive<U: Unit> : Signed<U>, Sign.Signed.Positive
        interface Negative<U: Unit> : Signed<U>, Sign.Signed.Negative
    }
    interface Unitless : Amount<Nothing>
    interface Unitful<U : Unit> : Amount<U>
    interface UnitOnly<U : Unit> : Amount<U>

    operator fun plus(other: Amount<U>) : Amount<U>
    operator fun minus(other: Amount<U>) : Amount<U>
    operator fun times(other: Amount<U>) : Amount<U>
    operator fun times(other: Amount.Unitless): Amount<U>
    operator fun div(other: Amount<U>) : Amount<U>
    operator fun <D : Unit> div(other: Amount<D>) : IRatio<U,D>
    operator fun <D : Unit> div(other: D) : IRatio.Rate.UnitDenominator<U,D>

    operator fun plus(other: BigDecimal) : Amount<U>
    operator fun minus(other: BigDecimal) : Amount<U>
    operator fun times(other: BigDecimal) : Amount<U>
    operator fun div(other: BigDecimal) : Amount<U>

    operator fun <D : Unit> plus(other: IRatio<U,D>) : IRatio<U,D>
    operator fun <D : Unit> minus(other: IRatio<U,D>) : IRatio<U,D>
    operator fun <N : Unit> times(other: IRatio<N,U>) : Amount<N>
    operator fun times(other: IRatio.Unitless) : Amount<U>
    operator fun <D : Unit> div(other: IRatio<U,D>) : Amount<D>

    fun toUnitless() : Amount.Unitless
    fun <Y: Unit> changeUnit(unit : Y) : Amount<Y>
}

sealed class Quantity< U : Unit>(override val quantity: BigDecimal, override val unit: U) : Amount<U> {
    override fun <D : Unit> div(other: Amount<D>): IRatio<U, D> = Ratio.of(this, other)

    override fun plus(other: BigDecimal): Amount<U> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun minus(other: BigDecimal): Amount<U> = Quantity.of(this.quantity - other, this.unit)

    override fun times(other: BigDecimal): Amount<U> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun div(other: BigDecimal): Amount<U> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <D : Unit> plus(other: IRatio<U, D>): IRatio<U, D> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <D : Unit> minus(other: IRatio<U, D>): IRatio<U, D> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <N : Unit> times(other: IRatio<N, U>): Amount<N> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <D : Unit> div(other: IRatio<U, D>): Amount<D> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <D : Unit> div(other: D): IRatio.Rate.UnitDenominator<U, D> = Ratio.withUnitDenominator(this, other)

    override fun times(other: IRatio.Unitless): Amount<U> = Quantity.of(this.quantity * other.netAmount(), this.unit)

    override fun toUnitless() : Amount.Unitless = Quantity.unitless(quantity)
    override fun <Y : Unit> changeUnit(unit: Y): Amount<Y> = Quantity.of(quantity, unit)

    override operator fun plus(other: Amount<U>) =  Quantity.of(this.quantity + other.quantity, unit)
    override operator fun minus(other: Amount<U>) =  Quantity.of(this.quantity - other.quantity, unit)
    override operator fun times(other: Amount<U>) =  Quantity.of(this.quantity * other.quantity, unit)
    override operator fun times(other: Amount.Unitless) =  Quantity.of(this.quantity * other.quantity, unit)
    override operator fun div(other: Amount<U>) = Quantity.of(this.quantity / other.quantity, unit)


    sealed class Unitless(override val quantity: BigDecimal) : Quantity<Nothing>(quantity, Nothing), Amount.Unitless {
        object Zero : Unitless(0.bd), Amount.Unsigned.Zero<Nothing>
        data class Absolute internal constructor(override val quantity: BigDecimal) : Unitless(quantity), Amount.Unsigned<Nothing>
        data class Positive internal constructor(override val quantity: BigDecimal) : Unitless(quantity), Amount.Signed.Positive<Nothing>
        data class Negative internal constructor(override val quantity: BigDecimal) : Unitless(quantity), Amount.Signed.Negative<Nothing>
    }

    class Zero<U : Unit>(override val unit: U) : Quantity<U>(0.bd, unit), Amount.Unitful<U>, Amount.Unsigned.Zero<U>
    data class Absolute<U : Unit> internal constructor(override val quantity: BigDecimal, override val unit: U) : Quantity<U>(quantity, unit),  Amount.Unitful<U>, Amount.Unsigned<U>
    data class UnitOnly<U : Unit> internal constructor(override val unit: U) : Quantity<U>(1.bd, unit), Amount.Unitful<U>, Amount.Unsigned<U>, Amount.UnitOnly<U>
    data class Positive<U : Unit> internal constructor(override val quantity: BigDecimal, override val unit: U) : Quantity<U>(quantity, unit), Amount.Unitful<U>, Amount.Signed.Positive<U>
    data class Negative<U : Unit> internal constructor(override val quantity: BigDecimal, override val unit: U) : Quantity<U>(quantity, unit), Amount.Unitful<U>, Amount.Signed.Negative<U>

    companion object {
        fun <U : Unit>  BigDecimal.ofUnit(unit : Unit) = of(this, unit)
        fun <U : Unit> of(amount: BigDecimal, unit: U): Amount<U> =
                when {
                    amount < 0.bd ->
                        when (unit) {
                            is Nothing -> negativeWithoutUnit(amount) as Amount<U>
                            else -> negativeWithUnit(amount, unit)
                        }
                    amount == 0.bd -> zero(unit)
                    amount == 1.bd -> unit(unit)
                    else ->
                        when (unit) {
                            is Nothing -> positiveWithoutUnit(amount) as Amount<U>
                            else -> positiveWithUnit(amount, unit)
                        }
                }


        fun <U : Unit> unsigned(amount: BigDecimal, unit: U): Amount<U> {
            val amountAbsolute = amount.abs()
            val result : Amount<U>
            result = when (unit) {
                is Nothing ->
                    when (amountAbsolute) {
                        0.bd -> zero() as Amount<U>
                        else -> unsignedWithoutUnit(amountAbsolute) as Amount<U>
                    }
                else ->
                    when (amountAbsolute) {
                        0.bd -> zero(unit)
                        1.bd -> unit(unit)
                        else -> unsignedWithUnit(amountAbsolute, unit)
                    }
            }
            return result
        }

        fun <U : Unit> unit(unit: U): Amount<U> = when (unit) { is Nothing -> positiveWithoutUnit(1.bd) as Amount<U> else -> Quantity.UnitOnly(unit)}
        fun <U : Unit> zero(unit: U): Amount.Unsigned.Zero<U> =
                when (unit) { is Nothing -> zero() as Amount.Unsigned.Zero<U> else -> Quantity.Zero(unit)
        }
        fun unitless(quantity: BigDecimal): Amount.Unitless =
                when {
                    quantity < 0.bd -> negativeWithoutUnit(quantity)
                    quantity == 0.bd -> zero()
                    else -> positiveWithoutUnit(quantity)
                }

        fun zero(): Quantity.Unitless.Zero = Quantity.Unitless.Zero
        private fun positiveWithoutUnit(amount: BigDecimal): Quantity.Unitless.Positive = Quantity.Unitless.Positive(amount)
        private fun <U : Unit> positiveWithUnit(amount: BigDecimal, unit: U): Quantity.Positive<U> = Quantity.Positive(amount, unit)
        private fun negativeWithoutUnit(amount: BigDecimal): Quantity.Unitless.Negative = Quantity.Unitless.Negative(amount)
        private fun <U : Unit> negativeWithUnit(amount: BigDecimal, unit: U): Quantity.Negative<U> = Quantity.Negative(amount, unit)
        private fun unsignedWithoutUnit(amount: BigDecimal): Quantity.Unitless.Absolute = Quantity.Unitless.Absolute(amount)
        private fun <U : Unit> unsignedWithUnit(amount: BigDecimal, unit: U): Quantity.Absolute<U> = Quantity.Absolute(amount, unit)
    }
}
