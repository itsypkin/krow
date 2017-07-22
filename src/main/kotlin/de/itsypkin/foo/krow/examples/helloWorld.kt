package de.itsypkin.foo.krow.examples

/**
 * @author itsypkin
 * @since 22.07.17
 */


fun main(args: Array<String>) {
    val s = "world"

    println(getHellWorld(s))
}

private fun getHellWorld(s: String) = "hello $s"
