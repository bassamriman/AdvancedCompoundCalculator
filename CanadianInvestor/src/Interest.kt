interface IInterestPerTime<T : TimeUnit<T>> : TemporalRate.UnitPerTime<Interest.UNIT, T>
interface IInterestFrequency<T : TimeUnit<T>> : IInterestPerTime<T>, TemporalRate.UnitPerTime.Frequency<Interest.UNIT, T>
interface ITimePerInterest<T : TimeUnit<T>> : TemporalRate.TimePerUnit<T, Interest.UNIT>
interface IInterestPeriod<T : TimeUnit<T>> : ITimePerInterest<T>, TemporalRate.TimePerUnit.Period<T, Interest.UNIT>

data class InterestFrequency<T : TimeUnit<T>> (override val numerator: Amount<Interest.UNIT>, override val timeUnit: T) : IInterestFrequency<T>, TemporalRate.UnitPerTime.Frequency<Interest.UNIT, T> by UnitPerTime.Frequency.per(numerator, timeUnit){

}

data class InterestPeriod<T : TimeUnit<T>>(override val timeNumerator : Amount<T>) : IInterestPeriod<T>, TemporalRate.TimePerUnit.Period<T, Interest.UNIT> by TimePerUnit.Period.per(timeNumerator, Interest.UNIT)