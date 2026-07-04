package com.troves.domain.utils

object PhoneUtils {
    private val EGYPTIAN_PHONE_REGEX = Regex("^(?:\\+20|0)?1[0125][0-9]{8}$")

    fun isValidEgyptianPhone(phone: String): Boolean {
        return EGYPTIAN_PHONE_REGEX.matches(phone.trim())
    }

    fun normalizeEgyptianPhone(phone: String): String {
        val trimmed = phone.trim()
        if (trimmed.isEmpty()) return ""
        if (!isValidEgyptianPhone(trimmed)) return trimmed

        return when {
            trimmed.startsWith("01") -> "+2$trimmed"
            trimmed.startsWith("1") -> "+20$trimmed"
            trimmed.startsWith("+2001") -> trimmed.replace("+2001", "+201")
            else -> trimmed
        }
    }
}
