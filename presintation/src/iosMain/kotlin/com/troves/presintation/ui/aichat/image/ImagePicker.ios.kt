package com.troves.presintation.ui.aichat.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSItemProvider
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): ImagePicker {
    val delegate = remember { PhotoPickerDelegate(onImagePicked) }

    return remember(delegate) {
        object : ImagePicker {
            override fun pick() {
                val config = PHPickerConfiguration().apply {
                    selectionLimit = 1
                    filter = PHPickerFilter.imagesFilter()
                }
                val picker = PHPickerViewController(configuration = config)
                picker.delegate = delegate

                UIApplication.sharedApplication.keyWindow?.rootViewController
                    ?.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class PhotoPickerDelegate(
    private val onImagePicked: (ByteArray) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)

        val result = didFinishPicking.firstOrNull() as? PHPickerResult ?: return
        val provider: NSItemProvider = result.itemProvider

        if (!provider.hasItemConformingToTypeIdentifier(IMAGE_UTI)) return
        provider.loadDataRepresentationForTypeIdentifier(IMAGE_UTI) { data, _ ->
            val bytes = (data as? NSData)?.toByteArray()
            if (bytes != null && bytes.isNotEmpty()) {
                dispatch_async(dispatch_get_main_queue()) { onImagePicked(bytes) }
            }
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        val size = length.toInt()
        if (size == 0) return ByteArray(0)
        val result = ByteArray(size)
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
        return result
    }

    private companion object {
        const val IMAGE_UTI = "public.image"
    }
}
