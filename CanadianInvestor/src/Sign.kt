sealed class Sign{
    object Positive: Sign()
    object Negative: Sign()
    interface Signed{
        fun sign() : Sign
        interface Positive : Signed{
            override fun sign() = Sign.Positive
        }
        interface Negative : Signed{
            override fun sign() = Sign.Negative
        }
    }

    interface Unsigned{
        interface Zero : Unsigned
    }
}

