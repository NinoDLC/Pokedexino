package fr.delcey.pokedexino.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.delcey.pokedexino.data.pokemons.local.PokemonDao
import fr.delcey.pokedexino.domain.pokemons.PokemonEntity

@Database(entities = [PokemonEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "AppDatabase"

        fun create(context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .build()
    }

    abstract fun getPokemonDao(): PokemonDao
}