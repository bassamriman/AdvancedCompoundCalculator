import java.util.*

data class CompoundCalculatorParams<U : Unit, T : TimeUnit<T>>(
        val amount : Amount<U>,
        val unit : U,
        val amountMutators : List<IAmountMutator.Temporal<U,T>>,
        val time : Amount<T>,
        val compoundResults : LinkedList<CompoundResult<U,T>>
)

data class CompoundResult<U : Unit, T : TimeUnit<T>>(
        val input : CompoundCalculatorParams<U, T>,
        val change : List<Variation<U>>,
        val time : Amount<T>,
        val amountAfterCompounding : Amount<U>
)


tailrec fun <U : Unit, T : TimeUnit<T>> compoundCalculator(input : CompoundCalculatorParams<U, T>) : CompoundResult<U, T>{
    if(input.time.quantity <= 0.bd){
        return CompoundResult(input, Collections.emptyList(), input.time, input.amount)
    }else{
        val variations = input.amountMutators
                .map {amountMutator -> amountMutator.toVariation(input.amount, input.time)}
                .filter {maybeVariation -> maybeVariation.isPresent}
                .map {variationOptional -> variationOptional.get()}
        val amountAfterCompounding = input.amount + variations.foldRight(0.bd * input.unit, { a,b -> a.apply(b)})
        val result = CompoundResult(input, variations, input.time, amountAfterCompounding )
        val newInput = CompoundCalculatorParams(amountAfterCompounding, input.unit, input.amountMutators, input.time - 1.bd, LinkedList.a(result, input.compoundResults))
        return compoundCalculator(newInput)
    }
}
