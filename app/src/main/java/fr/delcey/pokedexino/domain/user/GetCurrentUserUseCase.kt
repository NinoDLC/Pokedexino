package fr.delcey.pokedexino.domain.user

import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.delcey.pokedexino.data.user.UserRepository
import fr.delcey.pokedexino.domain.CRASHLYTICS_CUSTOM_KEY_USER
import fr.delcey.pokedexino.domain.image_generator.CreateImageFromInitialsUseCase
import fr.delcey.pokedexino.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

/**
 * Returns a [Flow] of [UserEntity], representing the information about the currently logged user, from Firebase
 */
class GetCurrentUserUseCase @Inject constructor(
    private val getLoggedUserUseCase: GetLoggedUserUseCase,
    private val createImageFromInitialsUseCase: CreateImageFromInitialsUseCase,
    private val userRepository: UserRepository,
    private val crashlytics: FirebaseCrashlytics,
) {

    operator fun invoke(): Flow<UserEntity?> = getLoggedUserUseCase().transformLatest { firebaseUser ->
        if (firebaseUser == null) {
            crashlytics.setCustomKey(CRASHLYTICS_CUSTOM_KEY_USER, "")
            emit(null)
        } else {
            crashlytics.setCustomKey(CRASHLYTICS_CUSTOM_KEY_USER, firebaseUser.uid)

            userRepository.getUser(firebaseUser.uid).collect {
                if (it.id != null && it.name != null && it.email != null) {
                    emit(
                        UserEntity(
                            id = it.id,
                            name = it.name,
                            email = it.email,
                            photoUrl = it.photoUrl ?: createImageFromInitialsUseCase(it.name),
                        )
                    )
                }
            }
        }
    }
}