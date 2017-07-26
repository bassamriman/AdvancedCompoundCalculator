import java.math.BigDecimal

/**
 * Created by Bassam on 6/10/2017.
 */
/*
interface Equatable {
    fun universalAmount() : UniversalMonies
    operator fun plus(other: Equatable) : Money = this.universalAmount() + other.universalAmount()
    operator fun minus(other: Equatable) : Money = this.universalAmount() - other.universalAmount()
    operator fun times(other: Equatable) : Money = this.universalAmount() * other.universalAmount()
    operator fun div(other: Equatable) : Money = this.universalAmount() / other.universalAmount()
}

interface Money : Equatable {
    fun currencyAmount() : BigDecimal
    fun currency() : Currency
}

interface Currency
object Universal : Currency
object CanadianDollar : Currency

open class Monies(open val amount: BigDecimal, val currency: Currency) : Money{
    override fun universalAmount(): UniversalMonies {
        return if (this is UniversalMonies) this else UniversalMonies.withAmount(convert(amount, currency, Universal))
    }
    override fun currencyAmount() = amount
    override fun currency() = currency

    fun to(target : Currency) : Monies {
        return when (target){
            currency -> this
            CanadianDollar -> CanadianDollars.withAmount(convert(amount, currency, target))
            else -> this
        }
    }

    companion object {
        fun withAmount(amount: BigDecimal, currency : Currency) {
            when (currency) {
                Universal -> UniversalMonies.withAmount(amount)
                CanadianDollar -> CanadianDollars.withAmount(amount)
            }
        }
        fun withUniversalMonies(universalMonies: UniversalMonies, currency : Currency) {
            when (currency) {
                Universal -> universalMonies
                CanadianDollar -> universalMonies.CAD
            }
        }
        fun withAmount(amount: Int, currency : Currency) = withAmount(amount.bd, currency)

        val UniversalMonies.UM: UniversalMonies
            get() = this
        val UniversalMonies.CAD: CanadianDollars
            get() = CanadianDollars.withAmount(convert(amount, currency, CanadianDollar))
    }
}

fun convert(amount: BigDecimal, from: Currency, to : Currency) : BigDecimal {
    return when (from) {
        to -> amount
        Universal -> when (to) { CanadianDollar -> amount * 2.bd else -> amount}
        else -> amount
    }
}

data class UniversalMonies(override val amount: BigDecimal) : Monies(amount, Universal){

    operator fun plus(other: UniversalMonies) =  UniversalMonies.withAmount(this.amount + other.amount)
    operator fun minus(other: UniversalMonies) =  UniversalMonies.withAmount(this.amount - other.amount)
    operator fun times(other: UniversalMonies) =  UniversalMonies.withAmount(this.amount * other.amount)
    operator fun div(other: UniversalMonies) = UniversalMonies.withAmount(this.amount / other.amount)

    companion object {

        fun withAmount(amount: BigDecimal) = UniversalMonies(amount)
        fun withAmount(amount: Int) = withAmount(amount.bd)
        val BigDecimal.UM: UniversalMonies
            get() = withAmount(this)
        val Int.UM: UniversalMonies
            get() = withAmount(this)
    }
}
data class CanadianDollars(override val amount: BigDecimal) : Monies(amount, CanadianDollar){
    companion object {
        fun withAmount(amount: BigDecimal) = CanadianDollars(amount)
        fun withAmount(amount: Int) = CanadianDollars(amount.bd)
        val BigDecimal.CAD: CanadianDollars
            get() = withAmount(this)
        val Int.CAD: CanadianDollars
            get() = withAmount(this)
    }
}


*/

