package de.itsypkin.foo.krow.examples

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.stream.Collector


/**
 * @author itsypkin
 * @since 22.07.17
 */


class MyNullableTest {

    @Test fun `null should be safe`() {
        val myMap = mapOf(
            "foo" to listOf("foo1", "foo2", "foo3"),
            "qax" to listOf("qax1", "qax2", "qax3")
        )

        val foo: List<String>? = myMap.get("foo")

        assertEquals(3, (foo?.size))

        val user = User("dsada", 12)

    }


}



data class User(val name: String, val age: Int?)


