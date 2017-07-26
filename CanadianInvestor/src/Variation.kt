sealed class Variation<U : Unit>(open val amount : Amount<U>, open val responsible : IAmountMutator<U>) {
    abstract fun apply(a : Amount<U>) : Amount<U>
    data class Decrease<U : Unit>(override val amount : Amount<U>, override val responsible: IAmountMutator.Signed.Negative<U>) : Variation<U>(amount, responsible){
        override fun apply(a : Amount<U>) : Amount<U> = a - amount
    }
    data class Increase<U : Unit>(override val amount : Amount<U>, override val responsible: IAmountMutator.Signed.Positive<U>) : Variation<U>(amount, responsible){
        override fun apply(a : Amount<U>) : Amount<U> = a + amount
    }
    companion object {
        fun <U : Unit> of(amount : Amount<U>, responsible : IAmountMutator<U>) :  Variation<U> = when (responsible) {
                is IAmountMutator.Signed.Positive<U> -> of(amount, responsible)
                is IAmountMutator.Signed.Negative<U> -> of(amount, responsible)
                else -> throw IllegalArgumentException()
            }
        fun <U : Unit> of(amount : Amount<U>, responsible : IAmountMutator.Signed.Positive<U>) :  Variation.Increase<U> = Variation.Increase(amount, responsible)
        fun <U : Unit> of(amount : Amount<U>, responsible : IAmountMutator.Signed.Negative<U>) :  Variation.Decrease<U> = Variation.Decrease(amount, responsible)
    }
}

