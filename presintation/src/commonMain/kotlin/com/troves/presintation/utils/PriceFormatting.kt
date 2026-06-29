package com.troves.presintation.utils


enum class Currency(val sign: String) {
    USD("$"),
    EGP("EGP"),
}

fun String.priceFormat(currency: Currency): String{

    val string = "${currency.sign}$this"
    return string

}