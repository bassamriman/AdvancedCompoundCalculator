/*data class Percent(val amount: Double) : Equatable {
    override fun universalAmount(): UniversalMonies = UniversalMonies.withAmount(amount.bd)

    operator fun plus(other: Percent) = (amount + other.amount).Percent
    operator fun times(other: Percent) = (amount * other.amount).Percent

    fun complement() = (1.0 - amount).Percent

    companion object {

        val FullPercentage = 100.Percent
        val ZeroPersent = 0.Percent

        fun percentage(amount: Double) = Percent(normalize(amount))
        fun percentage(amount: Int) = percentage(amount.toDouble())
        infix fun Percent.of(money: Money) : Money = this * money

        val Double.Percent : Percent
            get() = percentage(this)
        val Int.Percent : Percent
            get() = percentage(this)

        fun normalize(amount: Double): Double {
            return when {
                amount < 0 -> 0.0
                amount > 100 -> 1.0
                else -> amount/100
            }
        }
    }
}
*/