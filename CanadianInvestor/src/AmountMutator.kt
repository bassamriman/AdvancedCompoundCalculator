import java.util.*
import kotlin.collections.HashSet

interface IAmountMutator< U : Unit> : Sign.Signed {
    interface Signed<U : Unit>: IAmountMutator<U> {
        interface Positive<U : Unit> : IAmountMutator<U>, Signed<U>, Sign.Signed.Positive {
            override fun toVariation(a: Amount<U>): Variation.Increase<U>
        }
        interface Negative<U : Unit> : IAmountMutator<U>, Signed<U>, Sign.Signed.Negative {
            override fun toVariation(a: Amount<U>): Variation.Decrease<U>
        }
    }

    fun toVariation(a: Amount<U>): Variation<U>

    interface FixedMutation<U : Unit> : IAmountMutator<U> {
        val mutationAmount: Amount<U>
        interface Positive<U : Unit> : FixedMutation<U>, Signed.Positive<U>{
            override fun toVariation(a: Amount<U>): Variation.Increase<U> = Variation.of(mutationAmount, this)
        }
        interface Negative<U : Unit> : FixedMutation<U>, Signed.Negative<U>{
            override fun toVariation(a: Amount<U>): Variation.Decrease<U> = Variation.of(mutationAmount, this)
        }
 }

    interface VariableMutation<U : Unit> : IAmountMutator<U>  {
        fun mutationAmount(a: Amount<U>): Amount<U>
        interface Positive<U : Unit> : VariableMutation<U>, Signed.Positive<U>{
            override fun toVariation(a: Amount<U>): Variation.Increase<U> = Variation.of(mutationAmount(a), this)
        }
        interface Negative<U : Unit> : VariableMutation<U>, Signed.Negative<U>{
            override fun toVariation(a: Amount<U>): Variation.Decrease<U> = Variation.of(mutationAmount(a), this)
        }
    }

    interface Simple<U : Unit> :  IAmountMutator<U>, Signed<U>

    interface Temporal<U : Unit, T : TimeUnit<T>> : IAmountMutator<U>, Signed<U> {
        fun predicate(time : Amount<T>) : Boolean
        fun toVariation(amount: Amount<U>, time: Amount<T>) : Optional<Variation<U>> = if (predicate(time)) Optional.of(toVariation(amount)) else Optional.empty()

        interface OneTime<U : Unit, T : TimeUnit<T>> : Temporal<U, T> {
            val time: Amount<T>
            override fun predicate(time : Amount<T>) : Boolean = time == this.time
        }

        interface MultipleTimes<U : Unit, T : TimeUnit<T>> : Temporal<U, T> {
            val times: HashSet<Amount<T>>
            override fun predicate(time : Amount<T>) : Boolean = this.times.contains(time)
        }

        interface Recurring<U : Unit, T : TimeUnit<T>, TRecurrenceUnit : Unit> : Temporal<U,T> {
            val period: TemporalRate.TimePerUnit.Period<T, TRecurrenceUnit>
            override fun predicate(time : Amount<T>) : Boolean =  time.quantity % period.numerator.quantity == 0.bd
       }
    }
}

interface SingleFixedMutation<U : Unit, T : TimeUnit<T>> : IAmountMutator.Temporal.OneTime<U, T>, IAmountMutator.FixedMutation<U>
interface SingleVariableMutation<U : Unit, T : TimeUnit<T>> : IAmountMutator.Temporal.OneTime<U, T>, IAmountMutator.VariableMutation<U>
interface MultipleFixedMutation<U : Unit, T : TimeUnit<T>> : IAmountMutator.Temporal.MultipleTimes<U, T>, IAmountMutator.FixedMutation<U>
interface MultipleVariableMutation<U : Unit, T : TimeUnit<T>> : IAmountMutator.Temporal.MultipleTimes<U, T>, IAmountMutator.VariableMutation<U>
interface RecurringFixedMutation<U : Unit, T : TimeUnit<T>, R : Unit> : IAmountMutator.Temporal.Recurring<U, T, R>, IAmountMutator.FixedMutation<U>
interface RecurringVariableMutation<U : Unit, T : TimeUnit<T>, R : Unit> : IAmountMutator.Temporal.Recurring<U, T, R>, IAmountMutator.VariableMutation<U>

sealed class InterestMutation<U : Unit, T : TimeUnit<T>, IT : TimeUnit<IT>>(
        open val percentage: IRatio.Unitless.Percent,
        open val rate: IInterestPeriod<IT>,
        open val compoundPeriod: ICompoundPeriod<T>) : RecurringVariableMutation<U, T, Compound.UNIT>{
    abstract val interestPerCompoundRatio: IRatio<Interest.UNIT, Compound.UNIT>
    abstract val interestToCompoundTimeUnitRatio : IRatio<IT, T>
    fun interestToCompoundTimeUnitRatio(): IRatio<IT, T> = rate.timeNumerator.unit.conversionRatio(compoundPeriod.timeNumerator.unit).inverse()
    fun interestPerCompoundRatio(): IRatio<Interest.UNIT, Compound.UNIT> = rate.inverse() Xdec interestToCompoundTimeUnitRatio Xdec compoundPeriod
    override fun mutationAmount(a: Amount<U>): Amount<U> = (a * percentage / Interest.UNIT Xdec interestPerCompoundRatio) * Compound.UNIT

    data class Positive<U : Unit, T : TimeUnit<T>, IT : TimeUnit<IT>>(
            override val percentage: IRatio.Unitless.Percent,
            override val rate: IInterestPeriod<IT>,
            override val compoundPeriod: ICompoundPeriod<T>) : InterestMutation<U, T, IT>(percentage, rate, compoundPeriod), IAmountMutator.VariableMutation.Positive<U>{
        override val period: TemporalRate.TimePerUnit.Period<T, Compound.UNIT> = compoundPeriod
        override val interestToCompoundTimeUnitRatio: IRatio<IT, T> = interestToCompoundTimeUnitRatio()
        override val interestPerCompoundRatio: IRatio<Interest.UNIT, Compound.UNIT> = interestPerCompoundRatio()
    }
    data class Negative<U : Unit, T : TimeUnit<T>, IT : TimeUnit<IT>>(
            override val percentage: IRatio.Unitless.Percent,
            override val rate: IInterestPeriod<IT>,
            override val compoundPeriod: ICompoundPeriod<T>) : InterestMutation<U, T, IT>(percentage, rate, compoundPeriod), IAmountMutator.VariableMutation.Negative<U>
    {
        override val period: TemporalRate.TimePerUnit.Period<T, Compound.UNIT> = compoundPeriod
        override val interestToCompoundTimeUnitRatio: IRatio<IT, T> = interestToCompoundTimeUnitRatio()
        override val interestPerCompoundRatio: IRatio<Interest.UNIT, Compound.UNIT> = interestPerCompoundRatio()
    }
}


data class RecurrentContribution<U : Unit, T : TimeUnit<T>>(override val period: TemporalRate.TimePerUnit.Period<T, Recurrence.UNIT>,
                                                            override val mutationAmount: Amount<U>): RecurringFixedMutation<U, T, Recurrence.UNIT>, IAmountMutator.FixedMutation.Positive<U>
