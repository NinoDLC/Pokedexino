package fr.delcey.pokedexino.utils

import fr.delcey.pokedexino.CoroutineDispatcherProvider
import io.mockk.every
import io.mockk.mockk

fun TestCoroutineRule.getTestCoroutineDispatcherProvider() = mockk<CoroutineDispatcherProvider> {
    every { main } returns testCoroutineDispatcher
    every { io } returns testCoroutineDispatcher
}