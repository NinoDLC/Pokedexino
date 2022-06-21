package fr.delcey.pokedexino;

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class BuildConfigTest {

    @Test
    fun `application id should be consistent`() {
        // Then
        assertThat(BuildConfig.APPLICATION_ID).isEqualTo("fr.delcey.pokedexino")
    }
}