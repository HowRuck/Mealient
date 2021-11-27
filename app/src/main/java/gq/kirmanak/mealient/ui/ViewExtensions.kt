package gq.kirmanak.mealient.ui

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

@ExperimentalCoroutinesApi
fun SwipeRefreshLayout.refreshesLiveData(): LiveData<Unit> {
    val callbackFlow: Flow<Unit> = callbackFlow {
        val listener = SwipeRefreshLayout.OnRefreshListener {
            Timber.v("Refresh requested")
            trySend(Unit)
                .onFailure { Timber.e(it, "refreshesFlow: can't send refresh callback") }
                .onClosed { Timber.e(it, "refreshesFlow: flow has been closed") }
        }
        Timber.v("Adding refresh request listener")
        setOnRefreshListener(listener)
        awaitClose {
            Timber.v("Removing refresh request listener")
            setOnRefreshListener(null)
        }
    }
    return callbackFlow.asLiveData()
}

fun Activity.setSystemUiVisibility(isVisible: Boolean) {
    Timber.v("setSystemUiVisibility() called with: isVisible = $isVisible")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) setSystemUiVisibilityV30(isVisible)
    else setSystemUiVisibilityV1(isVisible)
}

@Suppress("DEPRECATION")
private fun Activity.setSystemUiVisibilityV1(isVisible: Boolean) {
    Timber.v("setSystemUiVisibilityV1() called with: isVisible = $isVisible")
    window.decorView.systemUiVisibility = if (isVisible) 0 else View.SYSTEM_UI_FLAG_FULLSCREEN
}

@RequiresApi(Build.VERSION_CODES.R)
private fun Activity.setSystemUiVisibilityV30(isVisible: Boolean) {
    Timber.v("setSystemUiVisibilityV30() called with: isVisible = $isVisible")
    val systemBars = WindowInsets.Type.systemBars()
    window.insetsController?.apply { if (isVisible) show(systemBars) else hide(systemBars) }
        ?: Timber.w("setSystemUiVisibilityV30: insets controller is null")
}

fun AppCompatActivity.setActionBarVisibility(isVisible: Boolean) {
    Timber.v("setActionBarVisibility() called with: isVisible = $isVisible")
    supportActionBar?.apply { if (isVisible) show() else hide() }
        ?: Timber.w("setActionBarVisibility: action bar is null")
}