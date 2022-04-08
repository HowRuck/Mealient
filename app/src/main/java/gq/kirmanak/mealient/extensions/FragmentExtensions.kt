package gq.kirmanak.mealient.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <T> Fragment.collectWithViewLifecycle(
    flow: Flow<T>,
    crossinline collector: suspend (T) -> Unit,
) = launchWithViewLifecycle { flow.collect(collector) }

fun Fragment.launchWithViewLifecycle(
    block: suspend CoroutineScope.() -> Unit,
) = viewLifecycleOwner.lifecycleScope.launch(block = block)
