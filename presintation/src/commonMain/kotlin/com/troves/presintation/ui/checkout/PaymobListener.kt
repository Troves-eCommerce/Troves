package com.troves.presintation.ui.checkout

interface PaymobListener {
    fun onSuccess(payResponse: HashMap<String, String?>)
    fun onFailure(msg: String?)
    fun onPending()
}