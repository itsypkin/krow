package de.itsypkin.foo.krow.examples

/**
 * @author itsypkin
 * @since 22.07.17
 */


fun main(args: Array<String>) {
    val s = "world"

    println(getHellWorld(s))

    doSomethingWithOne { it + 2 }
}

private fun getHellWorld(s: String) = "hello $s"


fun <T> doSomethingWithOne(block: (Int) -> T): T {
     val one = 1
    return block(one)
}