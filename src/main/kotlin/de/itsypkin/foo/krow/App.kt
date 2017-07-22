package de.itsypkin.foo.krow

import de.itsypkin.foo.krow.services.RedisService
import de.itsypkin.foo.krow.verticles.HttpVerticle
import io.vertx.core.Vertx

/**
 * @author itsypkin
 * @since 12.06.17
 */
object App {

    @JvmStatic
    fun main(args: Array<String>) {

        val vertx = Vertx.vertx()

        val redisService = RedisService(vertx)

        val httpVerticle = HttpVerticle(redisService)

        vertx.deployVerticle(httpVerticle) {
            if (it.succeeded()) {
                println("verticle deployed")
            } else {
                println("something went wrong")
                println(it.cause())
            }
        }

    }
}