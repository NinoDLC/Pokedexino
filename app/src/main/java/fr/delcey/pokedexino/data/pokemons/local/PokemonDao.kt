package fr.delcey.pokedexino.data.pokemons.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.delcey.pokedexino.domain.pokemons.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    // 1/ OFFSET keyword performs poorly in SQLite, check https://www2.sqlite.org/cvstrac/wiki?p=ScrollingCursor:
    // The way OFFSET works in SQLite is that it causes the sqlite3_step() function to ignore the first :index breakpoints that it sees. So,
    // for example, if :index is 1000, you are really reading in 1005 entries and ignoring all but the last 5. The net effect is that
    // scrolling starts to become sluggish as you get lower and lower in the list.
    // 2/ Room OFFSET keyword implementation is even worse, it will re-emit values to the flow even if the underlying values didn't change.
    // If data is appended at the end of the table (for example id == 101+), all the flows "targeting" ids 1 to 100 will re-emit for nothing.
    @Query("SELECT * FROM PokemonEntity WHERE id >= :offset ORDER BY id LIMIT :limit")
    fun getPokemonsAsFlow(limit: Long, offset: Long): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(pokemons: List<PokemonEntity>)
}
