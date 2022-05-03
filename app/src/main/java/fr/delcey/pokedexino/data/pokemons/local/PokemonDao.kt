package fr.delcey.pokedexino.data.pokemons.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.delcey.pokedexino.domain.pokemons.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM PokemonEntity ORDER BY id LIMIT :limit OFFSET :offset")
    fun getPokemonsAsFlow(limit: Long, offset: Long): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(pokemons: List<PokemonEntity>)
}
