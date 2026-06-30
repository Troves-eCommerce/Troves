package com.troves.presintation.core.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


interface StateHolder<S> {
    val state: StateFlow<S>

    val currentState: S get() = state.value

    fun updateState(transform: S.() -> S)
}


class DefaultStateHolder<S>(initialState: S) : StateHolder<S> {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state.asStateFlow()

    override fun updateState(transform: S.() -> S) {
        _state.update(transform)
    }
}