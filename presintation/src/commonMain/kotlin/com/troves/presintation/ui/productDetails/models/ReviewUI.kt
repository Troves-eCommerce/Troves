package com.troves.presintation.ui.productDetails.models

data class ReviewUi(
    val authorName: String,
    val rating: Int,
    val date: String = "",
    val comment: String = "",
)
