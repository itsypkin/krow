package de.itsypkin.foo.krow.verticles

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.itsypkin.foo.krow.User
import de.itsypkin.foo.krow.services.RedisService
import de.itsypkin.foo.krow.utils.await
import de.itsypkin.foo.krow.utils.createAsyncHandler
import de.itsypkin.foo.krow.utils.future
import de.itsypkin.foo.krow.utils.waitForIt
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.ext.web.Router
import io.vertx.ext.web.Router.router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import java.util.*

/**
 * @author itsypkin
 * @since 08.07.17
 */
@Suppress("unused")
class HttpVerticle(val redisService: RedisService) : AbstractVerticle() {
    override fun start(startFuture: Future<Void>?) {
        super.start()

        val router = createRouter()
        val port = 9090

        val server = vertx.createHttpServer()

        server.requestHandler { router.accept(it) }
            .listen(port) { res ->
                if (res.succeeded()) {
                    println("server is running on port $port")
                    startFuture?.complete()
                } else {
                    startFuture?.fail(res.cause())
                }
            }
    }


    fun createRouter() = Router.router(vertx).apply {
        get("/").handler(rootHandler())
        get("/users/:id").handler(getUser2())
        route("/users*").handler(BodyHandler.create())
        post("/users").handler(saveUser())
    }

    fun rootHandler() = Handler<RoutingContext> {
        it.response().end("Hi there")
    }

    fun saveUser() = Handler<RoutingContext> { rc ->
        val user = jacksonObjectMapper().readValue<User>(rc.bodyAsString)
        println("saving new user $user")

        val id = UUID.randomUUID()
        val values = mapOf(
                "name" to user.name,
                "age" to user.age.toString())

        redisService.hsetAll("users:$id", values).setHandler {
            if (it.succeeded()) {
                rc.response().end("user saved id: $id")
            } else {
                rc.response().setStatusCode(500).end("something went wrong ${it.cause().localizedMessage}")
            }
        }

    }


    fun getUser() = Handler<RoutingContext> { rc ->
        val id = rc.pathParam("id")

        val res = future {
            val key = "users:$id"

            val user = redisService.hgetall(key).await()

            user
        }

        res.setHandler {
            if (it.succeeded())
                rc.response().end(it.result().encode())
            else
                rc.response().setStatusCode(500).end(it.cause().localizedMessage)
        }
    }

    fun getUser2() = createAsyncHandler { rc ->
        val id = rc.pathParam("id")
        val key = "users:$id"

        println("legennnn...")

        println(waitForIt(vertx, 5000).await())

        val user = redisService.hgetall(key).await()

        user.encode()
    }

}