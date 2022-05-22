package fr.delcey.pokedexino.ui.pokemons

import android.app.Application
import com.google.common.truth.Truth.assertThat
import fr.delcey.pokedexino.domain.favorites.GetFavoritePokemonIdsUseCase
import fr.delcey.pokedexino.domain.favorites.UpdateIsPokemonFavoriteUseCase
import fr.delcey.pokedexino.domain.pokemons.GetPagedPokemonsUseCase
import fr.delcey.pokedexino.domain.user.GetCurrentUserUseCase
import fr.delcey.pokedexino.utils.TestCoroutineRule
import fr.delcey.pokedexino.utils.TestDefaults
import fr.delcey.pokedexino.utils.observeForTesting
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PokemonsViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val context: Application = mockk()
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val getPagedPokemonsUseCase: GetPagedPokemonsUseCase = mockk()
    private val getFavoritePokemonIdsUseCase: GetFavoritePokemonIdsUseCase = mockk()
    private val updateIsPokemonFavoriteUseCase: UpdateIsPokemonFavoriteUseCase = mockk()
    private val coroutineDispatcherProvider = testCoroutineRule.getCoroutineDispatcherProvider()

    private val pokemonsViewModel = PokemonsViewModel(
        context,
        getCurrentUserUseCase,
        getPagedPokemonsUseCase,
        getFavoritePokemonIdsUseCase,
        updateIsPokemonFavoriteUseCase,
        coroutineDispatcherProvider
    )

    @Before
    fun setUp() {
        clearAllMocks()

        every { getCurrentUserUseCase() } returns flowOf(null)
        every { getPagedPokemonsUseCase.get() } returns flowOf(TestDefaults.getGetPagedPokemonsUseCasePokemonListDto())
        every { getFavoritePokemonIdsUseCase() } returns flowOf(emptyList())
        coJustRun { updateIsPokemonFavoriteUseCase.invoke(any(), any()) }
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) {

            val value = it.value

            // Then
            assertThat(value).isNotNull()
            assertThat(value.items)
        }
    }
}