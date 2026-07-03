package com.troves.presintation.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.repository.AddressRepository
import com.troves.domain.repository.CartRepository
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val addressRepository: AddressRepository
) : ViewModel(),
    StateHolder<CheckoutUiState> by DefaultStateHolder(CheckoutUiState()),
    EffectPublisher<CheckoutEffect> by DefaultEffectPublisher() {

    init {
        combine(cartRepository.cartItems, addressRepository.addresses) { items, addresses ->
            val selected = state.value.selectedAddress 
                ?: addresses.firstOrNull { it.isDefault } 
                ?: addresses.firstOrNull()
            
            updateState { 
                copy(
                    cartItems = items, 
                    addresses = addresses, 
                    selectedAddress = selected,
                    isLoading = false
                ) 
            }
        }.launchIn(viewModelScope)
    }

    fun onIntent(intent: CheckoutIntent) {
        when (intent) {
            CheckoutIntent.OnBackClick -> sendEffect(CheckoutEffect.NavigateBack)
            CheckoutIntent.OnPlaceOrder -> placeOrder()
            CheckoutIntent.OnChangeAddressClick -> updateState { copy(showAddressSheet = true) }
            CheckoutIntent.OnDismissAddressSheet -> updateState { copy(showAddressSheet = false) }
            is CheckoutIntent.OnAddressSelected -> updateState { 
                copy(selectedAddress = intent.address, showAddressSheet = false) 
            }
            CheckoutIntent.OnAddNewAddress -> {
                updateState { copy(showAddressSheet = false) }
                sendEffect(CheckoutEffect.NavigateToNewAddress)
            }
        }
    }
    
    private fun placeOrder() {
        if (state.value.selectedAddress == null || state.value.cartItems.isEmpty()) return
        
        updateState { copy(isPlacingOrder = true) }
        viewModelScope.launch {
            // Simulate API call to place order
            kotlinx.coroutines.delay(1500)
            cartRepository.clearLocal()
            updateState { copy(isPlacingOrder = false) }
            sendEffect(CheckoutEffect.NavigateToOrderSuccess)
        }
    }
}
