package gq.kirmanak.mealient.ui.baseurl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gq.kirmanak.mealient.data.auth.AuthRepo
import gq.kirmanak.mealient.data.baseurl.ServerInfoRepo
import gq.kirmanak.mealient.data.recipes.RecipeRepo
import gq.kirmanak.mealient.logging.Logger
import gq.kirmanak.mealient.ui.OperationUiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseURLViewModel @Inject constructor(
    private val serverInfoRepo: ServerInfoRepo,
    private val authRepo: AuthRepo,
    private val recipeRepo: RecipeRepo,
    private val logger: Logger,
) : ViewModel() {

    private val _uiState = MutableLiveData<OperationUiState<Unit>>(OperationUiState.Initial())
    val uiState: LiveData<OperationUiState<Unit>> get() = _uiState

    fun saveBaseUrl(baseURL: String) {
        logger.v { "saveBaseUrl() called with: baseURL = $baseURL" }
        _uiState.value = OperationUiState.Progress()
        val hasPrefix = ALLOWED_PREFIXES.any { baseURL.startsWith(it) }
        var url = baseURL.takeIf { hasPrefix } ?: WITH_PREFIX_FORMAT.format(baseURL)
        url = url.trimStart().trimEnd { it == '/' || it.isWhitespace() }
        viewModelScope.launch { checkBaseURL(url) }
    }

    private suspend fun checkBaseURL(baseURL: String) {
        logger.v { "checkBaseURL() called with: baseURL = $baseURL" }
        if (baseURL == serverInfoRepo.getUrl()) {
            logger.d { "checkBaseURL: new URL matches current" }
            _uiState.value = OperationUiState.fromResult(Result.success(Unit))
            return
        }
        val result = serverInfoRepo.tryBaseURL(baseURL)
        if (result.isSuccess) {
            authRepo.logout()
            recipeRepo.clearLocalData()
        }
        logger.i { "checkBaseURL: result is $result" }
        _uiState.value = OperationUiState.fromResult(result)
    }

    companion object {
        private val ALLOWED_PREFIXES = listOf("http://", "https://")
        private const val WITH_PREFIX_FORMAT = "https://%s"
    }
}