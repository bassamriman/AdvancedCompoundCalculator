/*
import java.math.BigDecimal

interface Unit{

    interface OfType<U : Unit.Type> : Unit

    interface Measurable<U : Unit.Type, BSU : SubUnit.Type> : Unit.OfType<U>{
        fun baseSubUnit(): SubUnit.Base<U, BSU>
        fun unit() = this
    }

    interface SubUnit<U : Unit.Type, SU : Unit.SubUnit.Type, BSU : Unit.SubUnit.Type> : Measurable<U, BSU>{
        fun parentUnit() : Unit.Measurable<U, BSU> = super.unit()
        override fun unit(): SubUnit<U, SU, BSU> = this
        fun baseFactor(): BigDecimal
        fun baseUnitRatio() : IRatio.Rate.UnitDenominator<SubUnit.Base<U, BSU>, Unit.SubUnit<U, SU,BSU>> = Ratio.withUnitDenominator(Quantity.of(baseFactor(), parentUnit().baseSubUnit()), this)
        fun <TSU : SubUnit.Type> convertTo(subUnit : SubUnit<U, TSU, BSU>) : Amount<SubUnit<U, TSU, BSU>> = (Ratio.ofUnits(this, Unit.UNIT) * baseUnitRatio() * subUnit.baseUnitRatio().inverse()).numerator
        fun <TSU : SubUnit.Type> conversionRatio(subUnit : SubUnit<U, TSU, BSU>) : IRatio<SubUnit<U, TSU, BSU>, SubUnit<U, SU, BSU>> = Ratio.of(convertTo(subUnit), Quantity.unit(unit()))
        interface Base<U : Unit.Type, BSU : Unit.SubUnit.Type> : Unit.SubUnit<U, BSU, BSU>{
            override fun baseFactor(): BigDecimal = 1.bd
        }
        interface Type
    }

    interface Type{
        object UNIT : Unit.Type
    }

    object UNIT : Unit.OfType<Unit.Type.UNIT>
}
object Interest : Unit.Type{
    object UNIT : Unit.OfType<Interest>
}
object Recurrence : Unit.Type{
    object UNIT : Unit.OfType<Recurrence>
}
interface Compound : Unit.Type{
    object UNIT : Unit.OfType<Compound>
}
interface Currency :  Unit.Type{
    object UNIT : Currency, Unit.OfType<Compound>
}
*/
