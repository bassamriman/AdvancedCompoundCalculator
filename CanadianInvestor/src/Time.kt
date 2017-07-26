abstract class TimeUnit<Self : TimeUnit<Self>> : MultitonUnit<Time,Time.Second,Self>{
    override val unitType: Time = Time
}
interface TimeBaseUnit : BaseMultitonUnit<Time,Time.Second>

object Time : MultiUnitType<Time.Second, Time>{
    override val baseUnit: Second = Second
    object Year : TimeUnit<Year>(){
        override fun baseFactor() = 52.bd * Week.baseFactor()
    }
    object SemiAnnual : TimeUnit<SemiAnnual>(){
        override fun baseFactor() = 6.bd * Month.baseFactor()
    }
    object QuarterYear : TimeUnit<QuarterYear>(){
        override fun baseFactor() = 4.bd * Month.baseFactor()
    }
    object Month : TimeUnit<Month>(){
        override fun baseFactor() = 2.bd * SemiMonth.baseFactor()
    }
    object SemiMonth : TimeUnit<SemiMonth>(){
        override fun baseFactor() = 15.bd * Day.baseFactor()
    }
    object BiWeek : TimeUnit<BiWeek>(){
        override fun baseFactor() = 2.bd * Week.baseFactor()
    }
    object Week : TimeUnit<Week>(){
        override fun baseFactor() = 7.bd * Day.baseFactor()
    }
    object Day : TimeUnit<Day>(){
        override fun baseFactor() = 24.bd * Hour.baseFactor()
    }
    object Hour : TimeUnit<Hour>(){
        override fun baseFactor() = 60.bd * Minute.baseFactor()
    }
    object Minute : TimeUnit<Minute>(){
        override fun baseFactor() = 60.bd * Second.baseFactor()
    }
    object Second : TimeUnit<Second>(), TimeBaseUnit
}

interface TemporalRate {
    interface UnitPerTime< N : Unit, T : TimeUnit<T>> : IRatio<N,T>, TemporalRate {
        override val numerator : Amount<N>
        val timeDenominator : Amount<T>
        interface Frequency< N : Unit,  T : TimeUnit<T>> : UnitPerTime<N,T>, IRatio.Rate.UnitDenominator<N, T>{
            override val numerator : Amount<N>
            val timeUnit: T
            interface SingleOccurrence< N :Unit, T : TimeUnit<T>> : Frequency<N, T>{
                val numeratorUnit: N
                override val timeUnit: T
            }
        }
    }

    interface TimePerUnit<T : TimeUnit<T>, D : Unit> :  IRatio<T, D>, TemporalRate {
        val timeNumerator : Amount<T>
        override val denominator : Amount<D>
        interface Period< T : TimeUnit<T>, D : Unit> : TimePerUnit<T,D>, IRatio.Rate.UnitDenominator<T, D>{
            override val timeNumerator: Amount<T>
            val denominatorUnit: D
            interface SingleOccurrence< T : TimeUnit<T>, D :Unit> : Period<T, D>{
                val timeNumeratorUnit: T
                //override val timeUnit: T
            }
        }
    }
}

data class Period< T : TimeUnit<T>, D : Unit>(override val timeNumerator : Amount<T>, override val denominatorUnit : D) : IRatio.Rate.UnitDenominator<T,D> by Ratio.withUnitDenominator(timeNumerator, denominatorUnit), TemporalRate.TimePerUnit.Period<T,D>

sealed class UnitPerTime< N : Unit,  T : TimeUnit<T>>(override val numerator : Amount<N>, override val timeDenominator: Amount<T>) : Ratio.Rate<N, T>(numerator, timeDenominator), TemporalRate.UnitPerTime<N, T> {
    sealed class Frequency< N : Unit,  T : TimeUnit<T>>(override val numerator: Amount<N>, override val timeUnit: T) : UnitPerTime<N, T>(numerator, Quantity.unit(timeUnit)), TemporalRate.UnitPerTime.Frequency<N, T> {
        sealed class SingleOccurrence< N : Unit,  T : TimeUnit<T>>(numeratorUnit: N, timeUnit: T) : Frequency<N, T>(Quantity.unit(numeratorUnit), timeUnit), TemporalRate.UnitPerTime.Frequency.SingleOccurrence<N, T> {
            data class per< N : Unit,  T : TimeUnit<T>>(override val numeratorUnit: N, override val timeUnit: T) : SingleOccurrence<N, T>(numeratorUnit, timeUnit)
            data class Annualy< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.Year>(numeratorUnit, Time.Year)
            data class SemiAnnualy< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N,Time.SemiAnnual>(numeratorUnit, Time.SemiAnnual)
            data class Monthly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.Month>(numeratorUnit, Time.Month)
            data class SemiMonthly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.SemiMonth>(numeratorUnit, Time.SemiMonth)
            data class Weekly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.Week>(numeratorUnit, Time.Week)
            data class Daily< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.Day>(numeratorUnit, Time.Day)
            data class Hourly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.Hour>(numeratorUnit, Time.Hour)
            data class Secondly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.Second>(numeratorUnit, Time.Second)
        }

        data class per< N : Unit,  T : TimeUnit<T>>(override val numerator: Amount<N>, override val timeUnit: T) : Frequency<N, T>(numerator, timeUnit)
        data class perYear< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.Year>(numerator, Time.Year)
        data class perSemiYear< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.SemiAnnual>(numerator, Time.SemiAnnual)
        data class perMonth< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.Month>(numerator, Time.Month)
        data class perSemiMonth< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.SemiMonth>(numerator, Time.SemiMonth)
        data class perBiWeek< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.BiWeek>(numerator, Time.BiWeek)
        data class perWeek< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.Week>(numerator, Time.Week)
        data class perDay< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.Day>(numerator, Time.Day)
        data class perHour< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.Hour>(numerator, Time.Hour)
        data class perMinute< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.Minute>(numerator, Time.Minute)
        data class perSecond< N : Unit>(override val numerator: Amount<N>) : Frequency<N, Time.Second>(numerator, Time.Second)
    }
}

sealed class TimePerUnit<T : TimeUnit<T>, D : Unit>(override val timeNumerator : Amount<T>, override val denominator : Amount<D>) : Ratio.Rate<T, D>(timeNumerator, denominator), TemporalRate.TimePerUnit<T, D> {
    sealed class Period< T : TimeUnit<T>, D : Unit>(override val timeNumerator : Amount<T>, override val denominatorUnit : D)  : TimePerUnit<T, D>(timeNumerator, Quantity.unit(denominatorUnit)), TemporalRate.TimePerUnit.Period<T,D> {
        /*sealed class SingleOccurrence< N : Unit, T : TimeUnit<T>>(numeratorUnit: N, timeUnit: T) : Period<N, T>(Quantity.unit(numeratorUnit), timeUnit), TemporalRate.UnitPerTime.Frequency.SingleOccurrence<N, T> {
            data class per< N : Unit, T : TimeUnit<T>>(override val numeratorUnit: N, override val timeUnit: T) : SingleOccurrence<N, T>(numeratorUnit, timeUnit)
            data class Annualy< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, Time.Year>(numeratorUnit, Time.UNIT.Year)
            data class SemiAnnualy< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N,TimeUnit.SemiAnnual>(numeratorUnit, Time.UNIT.SemiAnnual)
            data class Monthly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, TimeUnit.Month>(numeratorUnit, Time.UNIT.Month)
            data class SemiMonthly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, TimeUnit.SemiMonth>(numeratorUnit, Time.UNIT.SemiMonth)
            data class Weekly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, TimeUnit.Week>(numeratorUnit, Time.UNIT.Week)
            data class Daily< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, TimeUnit.Day>(numeratorUnit, Time.UNIT.Day)
            data class Hourly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, TimeUnit.Hour>(numeratorUnit, Time.UNIT.Hour)
            data class Secondly< N : Unit>(override val numeratorUnit: N) : SingleOccurrence<N, TimeUnit.Second>(numeratorUnit, Time.UNIT.Second)
        }*/

        data class per< T : TimeUnit<T>, D : Unit>(override val timeNumerator : Amount<T>, override val denominatorUnit : D) : Period<T, D>(timeNumerator,denominatorUnit)
       /*
        data class perYear< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.Year>(numerator, Time.UNIT.Year)
        data class perSemiYear< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.SemiAnnual>(numerator, Time.UNIT.SemiAnnual)
        data class perMonth< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.Month>(numerator, Time.UNIT.Month)
        data class perSemiMonth< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.SemiMonth>(numerator, Time.UNIT.SemiMonth)
        data class perBiWeek< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.BiWeek>(numerator, Time.UNIT.BiWeek)
        data class perWeek< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.Week>(numerator, Time.UNIT.Week)
        data class perDay< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.Day>(numerator, Time.UNIT.Day)
        data class perHour< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.Hour>(numerator, Time.UNIT.Hour)
        data class perMinute< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.Minute>(numerator, Time.UNIT.Minute)
        data class perSecond< N : Unit>(override val numerator: Amount<N>) : Frequency<N, TimeUnit.Second>(numerator, Time.UNIT.Second)
        */
        }
}
