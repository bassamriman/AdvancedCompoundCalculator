import java.math.BigDecimal

interface Formula {
    fun <IU: Unit, OU: Unit, I : Amount<IU>, O : Amount<OU>> transformation(amount1: Amount<IU>) : Amount<OU>
    fun unitlessTransformation(amount1: BigDecimal, amount2: BigDecimal)
}

interface Formula1<IU: Unit, OU: Unit> {
    fun transformation(amount1: Amount<IU>) : Amount<OU>
    fun unitlessTransformation(amount1: BigDecimal, amount2: BigDecimal)
}




interface ArithmeticOperation
interface Operand
object AmountAddition : ArithmeticOperation{
    fun <U : Unit> transformation() : (Amount<U>, Amount<U>) -> Amount<U> = { a, b -> Quantity.of(a.quantity + b.quantity, a.unit)}
    fun <U : Unit> unitToUnitlessTransformation() : (Amount<U>, Amount<U>) -> BigDecimal = { a, b -> unitlessTransformation().invoke(a.quantity, b.quantity)}
    fun unitlessTransformation() : (BigDecimal, BigDecimal) -> BigDecimal = { a, b -> a + b}
}
