package com.troves.presintation.ui.aichat.image

/** Longest-edge cap applied to uploaded images so payloads stay small. */
const val MAX_IMAGE_DIMEN: Int = 1024

/** JPEG quality (0..100) used when re-encoding a picked image for upload. */
const val UPLOAD_JPEG_QUALITY: Int = 80

/**
 * Downscales [raw] so its longest edge is at most [MAX_IMAGE_DIMEN] and re-encodes it
 * as JPEG (~[UPLOAD_JPEG_QUALITY]). Returns the original bytes if decoding fails.
 * CPU-bound — call off the main thread.
 */
expect fun processImageForUpload(raw: ByteArray): ByteArray

/** Base64-encodes [bytes] with no line wrapping (raw base64, no `data:` prefix). */
expect fun encodeBase64(bytes: ByteArray): String
