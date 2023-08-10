package com.example.springjpaplayground.controller

import com.example.springjpaplayground.LOOM
import com.example.springjpaplayground.runOnLoom
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.measureTimedValue

@RestController
@RequestMapping("api/coroutines")
class CoroutineTestController {

    @GetMapping("/thread-sleep")
    fun test() = runOnLoom {
        repeat(1_000_000) {
            launch { blockingDbCall() }
        }
    }

    @GetMapping("/db-load-loom")
    fun dbLoad() = runOnLoom {
        supervisorScope {
            repeat(50_000) {
                launch { blockingDbCall() }
            }
        }

        // Takes ~2s
    }

    @GetMapping("/db-load-io")
    fun dbLoadPure() = runBlocking(Dispatchers.IO) {
        supervisorScope {
            repeat(50_000) {
                launch { blockingDbCall() }
            }
        }

        // Takes MAGNITUDES longer, as Dispatchers.IO is limited to 64 threads
        // meaning only 64 threads at once can be sleeping
    }

    private fun blockingDbCall() = Thread.sleep(1500L)

    @GetMapping("/multiple-microservices-example")
    fun simulateMultipleMicroservicesBlockingCalls() = runOnLoom {
        measureTimedValue {
            runCatching {
                coroutineScope {
                    val snailData = async { snailSuspendableMicroservice() }
                    val fastData = async { fastMicroservice() }
                    val slowData = async { slowMicroservice() }
                    val userData = async { errorsMicroservice() }

                    // Even though this blocks Thread for 5000 ms, we return 200 OK in 1200 ms (slowest blocking microservice + fallback)
                    // As this Kafka call has different scope, our coroutine scope is not waiting for it's completion
                    kafkaEventLoggingScope.launch { logActionIntoKafka() }

                    // Coroutine scope has to wait for the slowest BLOCKING async so this exception at ~1000ms
                    "${snailData.await()}-${fastData.await()}-${slowData.await()}-${userData.await()}"
                }
            }.getOrElse {
                kafkaEventLoggingScope.launch {
                    Thread.sleep(1000L)
                    println("Oh no, microservices down, we gotta serve at least something! Going back to fallback.")
                }
                // and another 200 ms
                fallbackService()
            }
        }.also { println(it.duration) }.value
    }

    private suspend fun snailSuspendableMicroservice() = delay(3500).let { "Snail" }
    private fun fastMicroservice() = Thread.sleep(300L).let { "Fast" }
    private fun slowMicroservice() = Thread.sleep(1000L).let { "Slow" }
    private fun errorsMicroservice(): String = Thread.sleep(500L).run { error("Oh no!") }

    // some kind of default cache maybe?
    private fun fallbackService(): String = Thread.sleep(200L).run { "Fallback" }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val kafkaDispatcher = Dispatchers.LOOM.limitedParallelism(2)
    private val kafkaExceptionHandler =
        CoroutineExceptionHandler { _, throwable -> println("Acting like a logger: ${throwable.message}") }
    private val kafkaEventLoggingScope = CoroutineScope(kafkaDispatcher + kafkaExceptionHandler)
    private fun logActionIntoKafka() = Thread.sleep(5000L).also { println("Kafka Kafka Kafka!") }
}
