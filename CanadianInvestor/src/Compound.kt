interface ICompoundPerTime<T : TimeUnit<T>> : TemporalRate.UnitPerTime<Compound.UNIT, T>
interface ICompoundFrequency<T : TimeUnit<T>> : ICompoundPerTime<T>, TemporalRate.UnitPerTime.Frequency<Compound.UNIT, T>
interface ITimePerCompound<T : TimeUnit<T>> : TemporalRate.TimePerUnit<T, Compound.UNIT>
interface ICompoundPeriod<T : TimeUnit<T>> : ITimePerCompound<T>, TemporalRate.TimePerUnit.Period<T, Compound.UNIT>
data class CompoundPeriod<T : TimeUnit<T>>(override val timeNumerator : Amount<T>) : ICompoundPeriod<T>, TemporalRate.TimePerUnit.Period<T, Compound.UNIT> by TimePerUnit.Period.per(timeNumerator, Compound.UNIT)