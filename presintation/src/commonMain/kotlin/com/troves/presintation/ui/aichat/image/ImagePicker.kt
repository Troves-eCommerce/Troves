package com.troves.presintation.ui.aichat.image

import androidx.compose.runtime.Composable


interface ImagePicker {
    fun pick()
}


@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): ImagePicker
