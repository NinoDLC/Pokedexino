package fr.delcey.pokedexino.domain.pokemons

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PokemonEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val imageUrl: String,
)
