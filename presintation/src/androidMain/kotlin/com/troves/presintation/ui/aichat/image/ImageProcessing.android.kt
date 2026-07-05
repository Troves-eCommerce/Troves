package com.troves.presintation.ui.aichat.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.max
import androidx.core.graphics.scale

actual fun processImageForUpload(raw: ByteArray): ByteArray {
    // 1) Read bounds only, to pick a power-of-two subsample close to the target.
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(raw, 0, raw.size, bounds)
    val longestEdge = max(bounds.outWidth, bounds.outHeight)
    if (longestEdge <= 0) return raw

    var sample = 1
    while (longestEdge / sample > MAX_IMAGE_DIMEN) sample *= 2

    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sample }
    val decoded = BitmapFactory.decodeByteArray(raw, 0, raw.size, decodeOptions) ?: return raw

    // 2) Exact-scale down if subsampling left it above the cap.
    val scaled = scaleToCap(decoded)

    // 3) Re-encode as JPEG.
    return ByteArrayOutputStream().use { stream ->
        scaled.compress(Bitmap.CompressFormat.JPEG, UPLOAD_JPEG_QUALITY, stream)
        if (scaled != decoded) scaled.recycle()
        decoded.recycle()
        stream.toByteArray()
    }
}

private fun scaleToCap(bitmap: Bitmap): Bitmap {
    val longest = max(bitmap.width, bitmap.height)
    if (longest <= MAX_IMAGE_DIMEN) return bitmap
    val ratio = MAX_IMAGE_DIMEN.toFloat() / longest
    val width = (bitmap.width * ratio).toInt().coerceAtLeast(1)
    val height = (bitmap.height * ratio).toInt().coerceAtLeast(1)
    return bitmap.scale(width, height)
}

actual fun encodeBase64(bytes: ByteArray): String =
    Base64.encodeToString(bytes, Base64.NO_WRAP)
