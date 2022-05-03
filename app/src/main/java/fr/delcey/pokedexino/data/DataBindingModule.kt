package fr.delcey.pokedexino.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.delcey.pokedexino.data.pokemons.PokemonRepositoryImpl
import fr.delcey.pokedexino.domain.pokemons.PokemonRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

    @Singleton
    @Binds
    abstract fun providePokemonRepository(impl: PokemonRepositoryImpl): PokemonRepository
}