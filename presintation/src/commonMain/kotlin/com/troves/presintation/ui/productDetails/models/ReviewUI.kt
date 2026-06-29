package com.troves.presintation.ui.productDetails.models

data class ReviewUi(
    val authorName: String,
    val rating: Int,         // 1-5 (whole stars)
    val date: String = "",
    val comment: String = "",
)