package fr.delcey.pokedexino.test_utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent

/**
 * Don't even ask about the runCurrent() part.
 * Forget everything you knew about coroutine testing before.
 * We were apes playing with mud it seems.
 * This change happened in coroutine testing 1.6.
 *
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/MIGRATION.md#replace-advancetimebyn-with-advancetimebyn-runcurrent
 *
 * Why is it needed ? *shrugs*
 */
fun TestScope.advanceTimeByAndRun(delayTimeMillis: Long) {
    testScheduler.advanceTimeBy(delayTimeMillis)
    runCurrent()
}

suspend fun <T> Flow<T>.firstWithInit(testScope: TestScope, afterStartedBlock: suspend () -> Unit): T {
    val deferred: Deferred<T> = testScope.async {
        first()
    }

    testScope.runCurrent()
    afterStartedBlock()

    return deferred.await()
}