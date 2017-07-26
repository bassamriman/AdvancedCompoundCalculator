fun main(args : Array<String>){
    //println((2.3.Percent of 100.CAD).universalAmount().CAD)
    //println((100.Percent * 100.UM).universalAmount())
 /*   val input = CompoundCalculatorParams(
            20000.bd * Currency.UNIT,
            Currency.UNIT,
            listOf(RecurrentContribution(Period(1000.bd * Time.Second, Recurrence.UNIT), -100.bd * Currency.UNIT)),
            20000.bd * Time.Second,
            LinkedList.Empty)
    */
    val input = CompoundCalculatorParams(
            40000.bd * Currency.UNIT,
            Currency.UNIT,
            listOf(InterestMutation.Positive(Ratio.percent(7.bd),InterestPeriod(1.bd * Time.Year), CompoundPeriod(1.bd * Time.Year)),
                    InterestMutation.Negative(Ratio.percent(7.bd),InterestPeriod(1.bd * Time.Year), CompoundPeriod(1.bd * Time.Year)),
                   RecurrentContribution(Period(1.bd * Time.Year, Recurrence.UNIT), 0.bd * Currency.UNIT)),
            20.bd * Time.Year,
            LinkedList.Empty)
    val result = compoundCalculator(input)
    println(result.amountAfterCompounding)
}


