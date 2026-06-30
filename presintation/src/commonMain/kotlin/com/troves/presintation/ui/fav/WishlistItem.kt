package com.troves.presintation.ui.fav

import org.jetbrains.compose.resources.DrawableResource

data class WishlistItem(
    val id: Int,
    val title: String,
    val category: String,
    val price: String,
    val imageRes: DrawableResource
)