package com.genesiseternity.incomemate.utils

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