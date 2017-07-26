import java.math.BigDecimal

val BigDecimal.Years :  Amount<Time.Year>
    get() = Quantity.of(this, Time.Year)
val BigDecimal.Seconds :  Amount<Time.Second>
    get() = Quantity.of(this, Time.Second)