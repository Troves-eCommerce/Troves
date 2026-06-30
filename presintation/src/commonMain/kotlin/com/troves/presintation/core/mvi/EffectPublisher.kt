package com.troves.presintation.core.mvi

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


interface EffectPublisher<E> {
    val effect: Flow<E>

    fun sendEffect(effect: E)
}

class DefaultEffectPublisher<E> : EffectPublisher<E> {
    private val _effect = Channel<E>(Channel.BUFFERED)
    override val effect: Flow<E> = _effect.receiveAsFlow()

    override fun sendEffect(effect: E) {
        _effect.trySend(effect)
    }
}
