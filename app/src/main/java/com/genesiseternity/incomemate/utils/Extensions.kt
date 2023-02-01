package com.genesiseternity.incomemate.utils

import android.os.Bundle

//region String extension
fun String.Companion.removeLastChar(str: String) : String
{
    return if (str.isNotEmpty()) str.replaceFirst(".$".toRegex(), "") else ""
}

fun String.replaceToRegex() : String
{
    return this.replace("[^\\d.-]".toRegex(), "")
}

fun String.Companion.isNumeric(word: String) : Boolean
{
    val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
    return word.matches(regex)
}
//endregion

//region Bundle extension
fun Bundle.putEnum(key:String, enum: Enum<*>) {
    putString(key, enum.name)
}

inline fun <reified T: Enum<T>> Bundle.getEnum(key:String): T? {
    return getString(key)?.let { enumValueOf<T>(it) }
}
//endregion

/*
fun Char.Companion.isDigit(c: Char): Boolean
{
    if (isOnlyDigits)
    {
        for (i in 0..word.length)
        {
            if (Char.isDigit(word[i]))
            {
                isOnlyDigits = false
            }
        }
    }

    return true
}
 */