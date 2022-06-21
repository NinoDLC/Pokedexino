package fr.delcey.pokedexino.ui.utils

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.mockk
import org.junit.Test

class NavArgProducerTest {

    @Test
    fun `getNavArgs should invoke fromSavedStateHandle function from nav arg`() {
        // Given
        val subject = NavArgProducer(mockk())

        // When
        val result = subject.getNavArgs(FakeNavArg::class)

        // Then
        assertThat(result).isEqualTo(FakeNavArg.fakeNavArgInstance)
    }

    class FakeNavArg : NavArgs {
        companion object {
            val fakeNavArgInstance = FakeNavArg()

            @Suppress("unused") // Used in reflection
            @JvmStatic
            fun fromSavedStateHandle(
                @Suppress("UNUSED_PARAMETER") savedStateHandle: SavedStateHandle // Used in reflection
            ): NavArgs {
                return fakeNavArgInstance
            }
        }
    }
}