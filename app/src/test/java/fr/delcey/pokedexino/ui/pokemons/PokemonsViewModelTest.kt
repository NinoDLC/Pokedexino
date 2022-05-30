package fr.delcey.pokedexino.ui.pokemons

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
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
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PokemonsViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
        every { getCurrentUserUseCase() } returns flowOf(null)
        every { getPagedPokemonsUseCase.get() } returns flowOf(getGetPagedPokemonsUseCasePokemonListDto())
        coJustRun { getPagedPokemonsUseCase.loadNextPage() }
        every { getFavoritePokemonIdsUseCase() } returns flowOf(emptyList())
        coJustRun { updateIsPokemonFavoriteUseCase.invoke(any(), any()) }
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // Given
        val pagedPokemonsDelay = 200L
        every { getPagedPokemonsUseCase.get() } returns flow {
            delay(pagedPokemonsDelay)
            emit(getGetPagedPokemonsUseCasePokemonListDto())
        }

        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(
                    PokemonsViewState(
                        items = emptyList(),
                        isRecyclerViewVisible = false,
                        isLoadingVisible = true
                    )
                )
            assertThat(pokemonsViewModel.viewActionEvents.value).isNull()
            coVerify(exactly = 1) {
                getPagedPokemonsUseCase.loadNextPage()
                getCurrentUserUseCase()
                getPagedPokemonsUseCase.get()
                getFavoritePokemonIdsUseCase()
            }
            verify { coroutineDispatcherProvider.io }
            confirmVerified(
                context,
                getCurrentUserUseCase,
                getPagedPokemonsUseCase,
                getFavoritePokemonIdsUseCase,
                updateIsPokemonFavoriteUseCase,
                coroutineDispatcherProvider
            )
        }
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(getDefaultPokemonsViewState())
            assertThat(pokemonsViewModel.viewActionEvents.value).isNull()
            coVerify(exactly = 1) {
                getPagedPokemonsUseCase.loadNextPage()
                getCurrentUserUseCase()
                getPagedPokemonsUseCase.get()
                getFavoritePokemonIdsUseCase()
            }
            verify { coroutineDispatcherProvider.io }
            confirmVerified(
                context,
                getCurrentUserUseCase,
                getPagedPokemonsUseCase,
                getFavoritePokemonIdsUseCase,
                updateIsPokemonFavoriteUseCase,
                coroutineDispatcherProvider
            )
        }
    }

    @Test
    fun `nominal case - end of paging`() = testCoroutineRule.runTest {
        // Given
        every { getPagedPokemonsUseCase.get() } returns flowOf(getGetPagedPokemonsUseCasePokemonListDto().copy(hasMoreData = false))

        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(
                    getDefaultPokemonsViewState().copy(
                        items = getDefaultPokemonsViewState().items - PokemonsViewState.Item.Loading
                    )
                )
            assertThat(pokemonsViewModel.viewActionEvents.value).isNull()
        }
    }

    @Test
    fun `error case - no items`() = testCoroutineRule.runTest {
        // Given
        every { getPagedPokemonsUseCase.get() } returns flowOf(getGetPagedPokemonsUseCasePokemonListDto().copy(pokemons = emptyList()))

        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(
                    PokemonsViewState(
                        items = emptyList(),
                        isRecyclerViewVisible = false,
                        isLoadingVisible = true,
                    )
                )
            assertThat(pokemonsViewModel.viewActionEvents.value).isNull()
        }
    }

    @Test
    fun `error case - too many consecutive IO exceptions at first launch`() = testCoroutineRule.runTest {
        // Given
        every { getPagedPokemonsUseCase.get() } returns flowOf(
            GetPagedPokemonsUseCase.PokemonListDto(
                pokemons = emptyList(),
                hasMoreData = false,
                failureState = GetPagedPokemonsUseCase.FailureState.TOO_MANY_ATTEMPTS,
            )
        )
        every { context.getString(R.string.pokemons_query_error_io) } returns "pokemons_query_error_io"

        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(
                    PokemonsViewState(
                        items = emptyList(),
                        isRecyclerViewVisible = false,
                        isLoadingVisible = true,
                    )
                )
            assertThat(pokemonsViewModel.viewActionEvents.value)
                .isNotNull()
                .isEqualTo(PokemonsViewAction.Toast("pokemons_query_error_io"))
        }
    }

    @Test
    fun `error case - too many consecutive IO exceptions after a first success`() = testCoroutineRule.runTest {
        // Given
        every { getPagedPokemonsUseCase.get() } returns flowOf(
            getGetPagedPokemonsUseCasePokemonListDto().copy(
                failureState = GetPagedPokemonsUseCase.FailureState.TOO_MANY_ATTEMPTS
            )
        )
        every { context.getString(R.string.pokemons_query_error_io) } returns "pokemons_query_error_io"

        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(getDefaultPokemonsViewState())
            assertThat(pokemonsViewModel.viewActionEvents.value)
                .isNotNull()
                .isEqualTo(PokemonsViewAction.Toast("pokemons_query_error_io"))
        }
    }

    @Test
    fun `error case - critical error at first launch`() = testCoroutineRule.runTest {
        // Given
        every { getPagedPokemonsUseCase.get() } returns flowOf(
            GetPagedPokemonsUseCase.PokemonListDto(
                pokemons = emptyList(),
                hasMoreData = false,
                failureState = GetPagedPokemonsUseCase.FailureState.CRITICAL,
            )
        )
        every { context.getString(R.string.pokemons_query_error_critical) } returns "pokemons_query_error_critical"

        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(
                    PokemonsViewState(
                        items = emptyList(),
                        isRecyclerViewVisible = false,
                        isLoadingVisible = true,
                    )
                )
            assertThat(pokemonsViewModel.viewActionEvents.value)
                .isNotNull()
                .isEqualTo(PokemonsViewAction.Toast("pokemons_query_error_critical"))
        }
    }

    @Test
    fun `error case - critical error after a first success`() = testCoroutineRule.runTest {
        // Given
        every { getPagedPokemonsUseCase.get() } returns flowOf(
            getGetPagedPokemonsUseCasePokemonListDto().copy(
                failureState = GetPagedPokemonsUseCase.FailureState.CRITICAL
            )
        )
        every { context.getString(R.string.pokemons_query_error_critical) } returns "pokemons_query_error_critical"

        // When
        pokemonsViewModel.viewStateLiveData.observeForTesting(this) { liveData ->

            // Then
            assertThat(liveData.value)
                .isNotNull()
                .isEqualTo(getDefaultPokemonsViewState())
            assertThat(pokemonsViewModel.viewActionEvents.value)
                .isNotNull()
                .isEqualTo(PokemonsViewAction.Toast("pokemons_query_error_critical"))
        }
    }

    @Test
    fun `verify onLoadMore`() = testCoroutineRule.runTest {
        // Given
        runCurrent()

        // When
        pokemonsViewModel.onLoadMore()
        runCurrent()

        // Then
        coVerify(exactly = 2) { // 2: Constructor and function invoke
            getPagedPokemonsUseCase.loadNextPage()
        }
        confirmVerified(getPagedPokemonsUseCase)
    }

    @Test
    fun `don't load twice if the first query did not finish yet`() = testCoroutineRule.runTest {
        // Given
        coEvery { getPagedPokemonsUseCase.loadNextPage() } coAnswers {
            delay(200)
        }
        advanceTimeByAndRun(200) // Constructor init
        pokemonsViewModel.onLoadMore()
        advanceTimeByAndRun(199)

        // When
        pokemonsViewModel.onLoadMore()
        runCurrent()

        // Then
        coVerify(exactly = 2) { // 2: Constructor and function invoke
            getPagedPokemonsUseCase.loadNextPage()
        }
        confirmVerified(getPagedPokemonsUseCase)
    }

    @Test
    fun `do load a second time if the first query finished`() = testCoroutineRule.runTest {
        // Given
        coEvery { getPagedPokemonsUseCase.loadNextPage() } coAnswers {
            delay(200)
        }
        advanceTimeByAndRun(200) // Constructor init
        pokemonsViewModel.onLoadMore()
        advanceTimeByAndRun(200)

        // When
        pokemonsViewModel.onLoadMore()
        runCurrent()

        // Then
        coVerify(exactly = 3) { // 3: Constructor and 2 function invoke
            getPagedPokemonsUseCase.loadNextPage()
        }
        confirmVerified(getPagedPokemonsUseCase)
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
    } + PokemonsViewState.Item.Loading,
    isRecyclerViewVisible = true,
    isLoadingVisible = false,
)
// endregion OUT