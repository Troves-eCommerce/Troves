package com.troves.domain.utils

object PhoneUtils {
    private val EGYPTIAN_PHONE_REGEX = Regex("^(?:\\+20|0)?1[0125][0-9]{8}$")

    fun isValidEgyptianPhone(phone: String): Boolean {
        val englishPhone = toEnglishNumerals(phone.trim())
        return EGYPTIAN_PHONE_REGEX.matches(englishPhone)
    }

    fun normalizeEgyptianPhone(phone: String): String {
        val trimmed = toEnglishNumerals(phone.trim())
        if (trimmed.isEmpty()) return ""
        if (!isValidEgyptianPhone(trimmed)) return trimmed

        return when {
            trimmed.startsWith("01") -> "+2$trimmed"
            trimmed.startsWith("1") -> "+20$trimmed"
            trimmed.startsWith("+2001") -> trimmed.replace("+2001", "+201")
            else -> trimmed
        }
    }

    private fun toEnglishNumerals(input: String): String {
        val arabicToEnglish = mapOf(
            '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
            '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9'
        )
        return input.map { arabicToEnglish[it] ?: it }.joinToString("")
    }
}
