package com.troves

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory

@Composable
actual fun InitializeCoil() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .build()
    }
}
