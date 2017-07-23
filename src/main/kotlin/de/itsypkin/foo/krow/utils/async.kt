package de.itsypkin.foo.krow.utils

import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import kotlin.coroutines.experimental.*

/**
 * @author itsypkin
 * @since 09.07.17
 */


suspend fun <T> Future<T>.await(): T =
    suspendCoroutine { continuation: Continuation<T> ->
        setHandler { res ->
            if (res.succeeded())
                continuation.resume(res.result())
            else
                continuation.resumeWithException(res.cause())
        }
    }

fun <T> future(context: CoroutineContext = EmptyCoroutineContext, block: suspend () -> T): Future<T> {

    val continuation: FutureContinuation<T> = FutureContinuation(context)
    block.startCoroutine(continuation)

    return continuation.future
}

private class FutureContinuation<T>(override val context: CoroutineContext): Continuation<T> {
    val future = Future.future<T>()

    override fun resume(value: T) {
        future.complete(value)
    }

    override fun resumeWithException(exception: Throwable) {
        future.fail(exception)
    }

}



fun createAsyncHandler(block: suspend (rc: RoutingContext) -> HttpResponse) = Handler<RoutingContext>  { rc ->
    future {
        block(rc)
    }.setHandler {
        if (it.succeeded()) {
            val httpResponse = it.result()
            rc.response().setStatusCode(httpResponse.code).end(httpResponse.body)
        } else
            rc.response().setStatusCode(500).end(it.cause().localizedMessage)
    }
}


fun waitForIt(vertx: Vertx, time: Long): Future<String> {
    val future = Future.future<String>()

    vertx.setTimer(time) {
        future.complete("dary")
    }

    return future
}


data class HttpResponse(val code: Int, val body: String)