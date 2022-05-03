package fr.delcey.pokedexino.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @DelicateCoroutinesApi
    @Singleton
    @Provides
    @GlobalCoroutineScope
    fun provideGlobalCoroutineScope(): CoroutineScope = GlobalScope
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalCoroutineScope