package fr.delcey.pokedexino.ui.pokemons

sealed class PokemonsViewAction {
    data class Toast(val message: String) : PokemonsViewAction()
}
