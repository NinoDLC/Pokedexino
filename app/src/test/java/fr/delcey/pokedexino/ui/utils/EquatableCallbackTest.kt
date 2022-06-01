package fr.delcey.pokedexino.ui.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import org.junit.Test

class EquatableCallbackTest {

    @Test
    fun `invoke should execute lambda code`() {
        // Given
        var executed = false
        val lambdaToBeExecuted = { executed = true }
        val subject = EquatableCallback(lambdaToBeExecuted)

        // When
        subject()

        // Then
        assertThat(executed).isTrue()
    }

    @Test
    fun `invoke twice should execute lambda code twice`() {
        // Given
        val executedCount = arrayOf(0)
        val lambdaToBeExecuted = { executedCount[0] = executedCount[0] + 1 }
        val subject = EquatableCallback(lambdaToBeExecuted)

        // When
        subject()
        subject()

        // Then
        assertThat(executedCount.first()).isEqualTo(2)
    }

    @Test
    fun `EquatableCallback should be equal to another EquatableCallback`() {
        // Given
        val subject = EquatableCallback {}
        val another = EquatableCallback {}

        // When
        val result = subject == another

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `EquatableCallback should not be equal to another object`() {
        // Given
        val subject = EquatableCallback {}
        val another = Any()

        // When
        val result = subject == another

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `EquatableCallback hashcode should be equal to another EquatableCallback hashcode`() {
        // Given
        val anotherHashCode = EquatableCallback {}.hashCode()
        val subject = EquatableCallback {}

        // When
        val result = subject.hashCode()

        // Then
        assertThat(result).isEqualTo(anotherHashCode)
    }

    @Test
    fun `EquatableCallback hashcode should not be equal to another object hashcode`() {
        // Given
        val anotherHashCode = Any().hashCode()
        val subject = EquatableCallback {}

        // When
        val result = subject.hashCode()

        // Then
        assertThat(result).isNotEqualTo(anotherHashCode)
    }
}