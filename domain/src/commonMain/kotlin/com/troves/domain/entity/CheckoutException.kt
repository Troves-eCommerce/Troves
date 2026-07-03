package com.troves.domain.entity;

data class CheckoutException(
     val errorDescription: String,
     val errorCode: String,
     val isRecoverable: Boolean
)