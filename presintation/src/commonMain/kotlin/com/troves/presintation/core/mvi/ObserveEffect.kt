package com.troves.presintation.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


@Composable
fun <T> ObserveEffect(
    effect: Flow<T>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    onEffect: (T) -> Unit,
) {
    val currentOnEffect by rememberUpdatedState(onEffect)
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            withContext(Dispatchers.Main.immediate) {
                effect.collect { currentOnEffect(it) }
            }
        }
    }
}
