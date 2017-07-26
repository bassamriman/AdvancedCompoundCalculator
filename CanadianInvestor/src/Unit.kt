import java.math.BigDecimal

interface Unit{
    interface Singleton<U : Unit.Type> : Unit
    interface Multiton<U : Unit.Type.MultiUnit<B, U>, B : Multiton.Base<U,B>, Self : Multiton<U,B,Self>> : Unit{
        val unitType: U
        fun baseUnit() : B = unitType.baseUnit
        fun baseFactor(): BigDecimal
        fun self() : Self = this as Self
        fun baseUnitRatio() : IRatio<B,Self>  {
            return baseFactor() * baseUnit() / (1.bd * self())
        }
        fun <T : Multiton<U,B,T>> converTo( target : T ) : Amount<T> = 1.bd * self() * conversionRatio(target)
        fun <T : Multiton<U,B,T>> conversionRatio(target : T) : IRatio<T, Self> =  baseUnitRatio() Xinc target.baseUnitRatio().inverse()
        interface Base<U : Unit.Type.MultiUnit<Self, U>, Self : Multiton.Base<U,Self>> : Multiton<U, Self, Self> {
            override fun baseUnit(): Self = this as Self
            override fun baseFactor() = 1.bd

        }
    }
    interface Type{
        interface SingleUnit : Type
        interface MultiUnit< B : Multiton.Base<Self, B>, Self : MultiUnit<B, Self>> : Type{
            val baseUnit : B
            fun unitType() : Unit.Type.MultiUnit<B, Self> = this
        }
    }
}
interface SingleUnit : Unit.Type.SingleUnit
interface MultiUnitType<B : Unit.Multiton.Base<Self, B>, Self : MultiUnitType<B, Self>> : Unit.Type.MultiUnit<B,Self>
interface SingletonUnit<T  : SingleUnit> : Unit.Singleton<T>
interface MultitonUnit<T : MultiUnitType<B,T>, B : BaseMultitonUnit<T,B>, Self : MultitonUnit<T,B,Self>> : Unit.Multiton<T,B,Self>
interface BaseMultitonUnit<T : MultiUnitType<Self,T>, Self : BaseMultitonUnit<T, Self>> : Unit.Multiton.Base<T,Self>


object None : SingleUnit
object Nothing : SingletonUnit<None>

abstract class DistanceUnit<Self : DistanceUnit<Self>> : MultitonUnit<Distance, Distance.Meter, Self>{
    override val unitType: Distance = Distance
}
interface DistanceBaseUnit : BaseMultitonUnit<Distance, Distance.Meter>

object Distance : MultiUnitType<Distance.Meter, Distance> {
    override val baseUnit: Meter = Meter
    object Meter : DistanceUnit<Meter>(), DistanceBaseUnit
    object KiloMeter : DistanceUnit<KiloMeter>(){
        override fun baseFactor(): BigDecimal = 1000.bd
    }
}

object Interest : SingleUnit{
    object UNIT : SingletonUnit<Interest>
}
object Recurrence : SingleUnit{
    object UNIT : SingletonUnit<Recurrence>
}
interface Compound : SingleUnit{
    object UNIT : SingletonUnit<Compound>
}
interface Currency :  SingleUnit{
    object UNIT : Currency, SingletonUnit<Compound>
}
