package fr.delcey.pokedexino.data

import com.google.firebase.firestore.FirebaseFirestore
import fr.delcey.pokedexino.data.utils.FirebaseNodeResolver
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class FirebaseNodeResolverTest {

    private val firebaseFirestore = mockk<FirebaseFirestore> {
        every { document(any()) } returns mockk()
        every { collection(any()) } returns mockk()
    }

    private val firebaseNodeResolver = FirebaseNodeResolver(firebaseFirestore)

    @Test
    fun getUsersCollection() {
        // When
        firebaseNodeResolver.getUsersCollection()

        // Then
        verify(exactly = 1) {
            firebaseFirestore.collection("users")
        }
    }

    @Test
    fun getUserDocument() {
        // When
        firebaseNodeResolver.getUserDocument("dXNlcklk")

        // Then
        verify(exactly = 1) {
            firebaseFirestore.document("users/dXNlcklk")
        }
    }

    @Test
    fun getFavoritePokemonsCollection() {
        // When
        firebaseNodeResolver.getFavoritePokemonsCollection("dXNlcklk")

        // Then
        verify(exactly = 1) {
            firebaseFirestore.collection("users/dXNlcklk/favorite_pokemons")
        }
    }
}