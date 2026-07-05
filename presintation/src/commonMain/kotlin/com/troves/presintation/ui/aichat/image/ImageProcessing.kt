package com.troves.presintation.ui.aichat.image

const val MAX_IMAGE_DIMEN: Int = 1024

const val UPLOAD_JPEG_QUALITY: Int = 80

expect fun processImageForUpload(raw: ByteArray): ByteArray

expect fun encodeBase64(bytes: ByteArray): String
