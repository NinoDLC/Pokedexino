package fr.delcey.pokedexino.domain.pokemons

interface PokemonRepository {
    suspend fun getPokemonByIdOrName(pokemonId: String): PokemonEntity?

    suspend fun getPagedPokemons(offset: Int, limit: Int): List<PokemonEntity>
}