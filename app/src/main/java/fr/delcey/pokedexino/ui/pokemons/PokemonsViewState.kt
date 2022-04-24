package fr.delcey.pokedexino.ui.pokemons

import androidx.annotation.DrawableRes
import fr.delcey.pokedexino.ui.utils.EquatableCallback

data class PokemonsViewState(
    val items: List<Item>,
) {
    sealed class Item(val type: Type) {
        enum class Type {
            LOADING,
            CONTENT,
        }

        data class Content(
            val pokemonId: String,
            val pokemonName: String,
            val pokemonImageUrl: String,
            @DrawableRes
            val starResourceDrawable: Int,
            val onCardClicked: EquatableCallback,
            val onFavoriteButtonClicked: EquatableCallback,
        ) : Item(Type.CONTENT)

        data class Loading(
            val onDisplayed: EquatableCallback,
        ) : Item(Type.LOADING)
    }
}