package gq.kirmanak.mealient.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gq.kirmanak.mealient.data.auth.AuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepo: AuthRepo,
) : ViewModel() {

    private val authRequestsFlow = MutableStateFlow(false)
    private val showLoginButtonFlow = MutableStateFlow(false)
    private val authenticationStateFlow = combine(
        authRequestsFlow,
        showLoginButtonFlow,
        authRepo.isAuthorizedFlow,
        AuthenticationState::determineState
    )
    val authenticationStateLive: LiveData<AuthenticationState>
        get() = authenticationStateFlow.asLiveData()
    var authRequested: Boolean by authRequestsFlow::value
    var showLoginButton: Boolean by showLoginButtonFlow::value

    init {
        viewModelScope.launch {
            authRequestsFlow.collect { isRequested ->
                // Clear auth token on logout request
                if (!isRequested) authRepo.logout()
            }
        }
    }

    suspend fun authenticate(username: String, password: String): Result<Unit> = runCatching {
        authRepo.authenticate(username, password)
    }.onFailure {
        Timber.e(it, "authenticate: can't authenticate")
    }
}