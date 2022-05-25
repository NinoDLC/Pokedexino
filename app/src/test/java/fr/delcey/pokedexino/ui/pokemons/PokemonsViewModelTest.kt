package fr.delcey.pokedexino.ui.pokemons

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import fr.delcey.pokedexino.R
import fr.delcey.pokedexino.domain.favorites.GetFavoritePokemonIdsUseCase
import fr.delcey.pokedexino.domain.favorites.UpdateIsPokemonFavoriteUseCase
import fr.delcey.pokedexino.domain.pokemons.GetPagedPokemonsUseCase
import fr.delcey.pokedexino.domain.user.GetCurrentUserUseCase
import fr.delcey.pokedexino.utils.TestCoroutineRule
import fr.delcey.pokedexino.utils.advanceTimeByAndRun
import fr.delcey.pokedexino.utils.defaults.DEFAULT_GET_PAGED_POKEMONS_USE_CASE_POKEMON_COUNT
import fr.delcey.pokedexino.utils.defaults.getGetPagedPokemonsUseCasePokemonListDto
import fr.delcey.pokedexino.utils.defaults.getPokemonImageUrl
import fr.delcey.pokedexino.utils.defaults.getPokemonName
import fr.delcey.pokedexino.utils.observeForTesting
import io.mockk.clearAllMocks
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PokemonsViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object {
        private const val EXPECTED_PAGED_POKEMONS_DELAY = 200L
    }

    private val context: Application = mockk()
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val getPagedPokemonsUseCase: GetPagedPokemonsUseCase = mockk()
    private val getFavoritePokemonIdsUseCase: GetFavoritePokemonIdsUseCase = mockk()
    private val updateIsPokemonFavoriteUseCase: UpdateIsPokemonFavoriteUseCase = mockk()
    private val coroutineDispatcherProvider = testCoroutineRule.getCoroutineDispatcherProvider()

    private val pokemonsViewModel = PokemonsViewModel(
        context = context,
        getCurrentUserUseCase = getCurrentUserUseCase,
        getPagedPokemonsUseCase = getPagedPokemonsUseCase,
        getFavoritePokemonIdsUseCase = getFavoritePokemonIdsUseCase,
        updateIsPokemonFavoriteUseCase = updateIsPokemonFavoriteUseCase,
        coroutineDispatcherProvider = coroutineDispatcherProvider
    )

    @Before
    fun setUp() {
        clearAllMocks()

        every { getCurrentUserUseCase() } returns flowOf(null)
        every { getPagedPokemonsUseCase.get() } returns flow {
            delay(EXPECTED_PAGED_POKEMONS_DELAY)
            emit(getGetPagedPokemonsUseCasePokemonListDto())
        }
        coJustRun { getPagedPokemonsUseCase.loadNextPage() }
        every { getFavoritePokemonIdsUseCase() } returns flowOf(emptyList())
        coJustRun { updateIsPokemonFavoriteUseCase.invoke(any(), any()) }
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) {

            // Then
            assertThat(it.value)
                .isNotNull()
                .isEqualTo(
                    PokemonsViewState(
                        items = emptyList(),
                        isRecyclerViewVisible = false,
                        isEmptyStateVisible = false,
                        isLoadingVisible = true
                    )
                )
        }
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) {
            advanceTimeByAndRun(EXPECTED_PAGED_POKEMONS_DELAY)

            // Then
            assertThat(it.value)
                .isNotNull()
                .isEqualTo(getDefaultPokemonsViewState())
        }
    }
}

// region OUT
private fun getDefaultPokemonsViewState() = PokemonsViewState(
    items = List(DEFAULT_GET_PAGED_POKEMONS_USE_CASE_POKEMON_COUNT) { index ->
        PokemonsViewState.Item.Content(
            pokemonId = index.toLong(),
            pokemonName = getPokemonName(index),
            pokemonImageUrl = getPokemonImageUrl(index),
            favoriteResourceDrawable = R.drawable.ic_star_outline_24,
            isFavoriteEnabled = false,
            onCardClicked = mockk(),
            onFavoriteButtonClicked = mockk()
        )
    },
    isRecyclerViewVisible = true,
    isEmptyStateVisible = false,
    isLoadingVisible = false,
)
// endregion OUT