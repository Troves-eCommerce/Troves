package com.troves.presintation.core.time

import platform.Foundation.NSDate
import platform.Foundation.NSISO8601DateFormatter

actual fun nowIso8601(): String = NSISO8601DateFormatter().stringFromDate(NSDate())
