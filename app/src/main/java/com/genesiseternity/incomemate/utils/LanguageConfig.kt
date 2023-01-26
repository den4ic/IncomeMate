package com.genesiseternity.incomemate.utils

import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

class LanguageConfig(var resources: Resources)
{
    fun setLanguage(languageId: Int)
    {
        when (languageId)
        {
            0 -> setLocale("en") // Английский
            1 -> setLocale("") // Арабский
            2 -> setLocale("") // Испанский
            3 -> setLocale("it") // Итальянский
            4 -> setLocale("zh") // Китайский
            5 -> setLocale("") // Корейский
            6 -> setLocale("") // Немецкий
            7 -> setLocale("") // Португальский
            8 -> setLocale("ru") // Русский
            9 -> setLocale("") // Сербский
            10 -> setLocale("") // Турецкий
            11 -> setLocale("fr") // Французский
            12 -> setLocale("") // Японский
        }
    }

    private fun setLocale(language: String)
    {
        val resources: Resources = this.resources
        val configuration: Configuration = resources.configuration
        val locale: Locale = Locale(language)
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}