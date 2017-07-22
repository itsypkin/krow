package de.itsypkin.foo.krow.services

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.redis.RedisOptions
import io.vertx.redis.RedisClient

/**
 * @author itsypkin
 * @since 08.07.17
 */
class RedisService(vertx: Vertx) {
    var redis: RedisClient? = null

    init {
        val config = RedisOptions(address = "127.0.0.1")

        redis = RedisClient.create(vertx, config)
    }


    fun hset(keyName: String, field: String, value: String): Future<Long> {
        val future = Future.future<Long>()

        redis?.hset(keyName, field, value, future.completer())
        return future
    }


    fun hsetAll(keyName: String, values: Map<String, String>): Future<Unit> {
        val future = Future.future<Unit>()
        val futures = values.entries.map { (field, value) -> hset(keyName, field, value) }

        CompositeFuture.all(futures).setHandler {
            if (it.succeeded()) {
                future.complete()
            } else {
                future.fail(it.cause())
            }
        }

        return future
    }


    fun hgetall(keyName: String): Future<JsonObject> {
        val future = Future.future<JsonObject>()

        redis?.hgetall(keyName, future.completer())

        return future
    }
}