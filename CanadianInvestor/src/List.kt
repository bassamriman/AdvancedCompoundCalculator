import kotlin.Nothing

sealed class LinkedList<out X> : Iterable<X>, Iterator<X> {
    override fun iterator(): Iterator<X> = this

    data class Node<out X>(val head: X, val tail: LinkedList<X>) : LinkedList<X>(){
        override fun hasNext(): Boolean = tail != Empty
        override fun next(): X = if(tail is Node) tail.head else throw NoSuchElementException()
    }
    object Empty : LinkedList<Nothing>(){
        override fun hasNext(): Boolean = false
        override fun next(): Nothing = throw NoSuchElementException()
    }
    companion object {
        fun <X> a(head : X) : LinkedList<X> = Node(head, Empty)
        fun <X> a(head : X, tail: LinkedList<X>) : LinkedList<X> = Node(head, tail)
        //fun <X> a(vararg heads : X) = if(heads.isEmpty()) Empty else Node(heads.first(), a(heads.asIterable().first()))
    }
}

