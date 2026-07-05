package com.troves.presintation.ui.aichat.image

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.dataWithBytes
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

/**
 * NOTE: Kotlin/Native iOS targets only compile on macOS. Verify the interop
 * (`UIImage.size` via `useContents`, `NSData` bridging) there.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun processImageForUpload(raw: ByteArray): ByteArray {
    if (raw.isEmpty()) return raw
    val image = UIImage(data = raw.toNSData()) ?: return raw
    val resized = image.scaledToCap(MAX_IMAGE_DIMEN.toDouble())
    val jpeg = UIImageJPEGRepresentation(resized, UPLOAD_JPEG_QUALITY / 100.0) ?: return raw
    return jpeg.toByteArray()
}

@OptIn(ExperimentalForeignApi::class)
actual fun encodeBase64(bytes: ByteArray): String =
    bytes.toNSData().base64EncodedStringWithOptions(0u)

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.scaledToCap(cap: Double): UIImage {
    val (width, height) = size.useContents { width to height }
    val longest = maxOf(width, height)
    if (longest <= cap) return this

    val ratio = cap / longest
    val newWidth = width * ratio
    val newHeight = height * ratio

    UIGraphicsBeginImageContextWithOptions(CGSizeMake(newWidth, newHeight), false, 1.0)
    drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
    val result = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return result ?: this
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val result = ByteArray(size)
    result.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}
