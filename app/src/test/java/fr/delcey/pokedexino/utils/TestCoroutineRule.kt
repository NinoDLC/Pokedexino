package fr.delcey.pokedexino.utils

import fr.delcey.pokedexino.domain.utils.CoroutineDispatcherProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestCoroutineRule : TestRule {
    val testCoroutineDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testCoroutineDispatcher)

    override fun apply(base: Statement, description: Description) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testCoroutineDispatcher)

            base.evaluate()

            Dispatchers.resetMain()
        }
    }

    fun runTest(block: suspend TestScope.() -> Unit) = testScope.runTest { block() }

    fun getCoroutineDispatcherProvider() = mockk<CoroutineDispatcherProvider> {
        every { main } returns testCoroutineDispatcher
        every { io } returns testCoroutineDispatcher
        every { default } returns testCoroutineDispatcher
    }
}