package fr.delcey.pokedexino.ui.main

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.pokedexino.CoroutineDispatcherProvider
import fr.delcey.pokedexino.R
import fr.delcey.pokedexino.domain.user.CreateUserUseCase
import fr.delcey.pokedexino.domain.user.GetCurrentUserUseCase
import fr.delcey.pokedexino.domain.user.SignOutUserUseCase
import fr.delcey.pokedexino.ui.utils.SingleLiveEvent
import fr.delcey.pokedexino.ui.utils.combine
import fr.delcey.pokedexino.ui.utils.loge
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val context: Application,
    private val createUserUseCase: CreateUserUseCase,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    companion object {
        private const val GOOGLE_AUTH_REQUEST_CODE = 42
    }

    private val isLoggingInMutableLiveData = MutableLiveData(false)

    val viewStateLiveData: LiveData<MainViewState> = combine(
        getCurrentUserUseCase().asLiveData(coroutineDispatcherProvider.io),
        isLoggingInMutableLiveData
    ) { currentUser, isLoggingIn ->
        if (isLoggingIn == null) return@combine

        when {
            currentUser == null -> if (isLoggingIn) {
                emit(
                    MainViewState(
                        animateHeaderChange = true,
                        isLoginButtonVisible = false,
                        isLogoutButtonVisible = false,
                        isLoadingVisible = true,
                        avatarUrl = null,
                        userName = null,
                        userEmail = null
                    )
                )
            } else {
                emit(
                    MainViewState(
                        animateHeaderChange = false,
                        isLoginButtonVisible = true,
                        isLogoutButtonVisible = false,
                        isLoadingVisible = false,
                        avatarUrl = null,
                        userName = null,
                        userEmail = null
                    )
                )
            }
            isLoggingIn -> isLoggingInMutableLiveData.value = false
            else -> emit(
                MainViewState(
                    animateHeaderChange = true,
                    isLoginButtonVisible = false,
                    isLogoutButtonVisible = true,
                    isLoadingVisible = false,
                    avatarUrl = currentUser.photoUrl,
                    userName = currentUser.name,
                    userEmail = currentUser.email
                )
            )
        }
    }

    val viewActionLiveEvent = SingleLiveEvent<MainViewAction>()

    fun onConnectButtonClicked() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        val googleSignInIntent = GoogleSignIn.getClient(context, googleSignInOptions).signInIntent

        viewActionLiveEvent.value = MainViewAction.NavigateForResult(googleSignInIntent, GOOGLE_AUTH_REQUEST_CODE)
    }

    fun onActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode == GOOGLE_AUTH_REQUEST_CODE) {
            viewModelScope.launch(coroutineDispatcherProvider.io) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    val user = FirebaseAuth
                        .getInstance()
                        .signInWithCredential(credential)
                        .await()
                        .user ?: FirebaseAuth.getInstance().currentUser

                    if (user == null || !createUserUseCase(user)) {
                        loge("Impossible to reach Firebase or Firestore… User = [$user]")
                        displayToast(context.getString(R.string.firestore_error))
                    }
                } catch (exception: Exception) {
                    loge("Google sign in failed", exception)
                    displayToast(context.getString(R.string.aborted_login_with_google))
                }
            }
        }
    }

    fun onDisconnectButtonClicked() {
        signOutUserUseCase.invoke()
    }

    private suspend fun displayToast(message: String) {
        withContext(coroutineDispatcherProvider.main) {
            viewActionLiveEvent.value = MainViewAction.Toast(message)
        }
    }
}