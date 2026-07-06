package com.troves.presintation.ui.aichat

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun nowEpochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
