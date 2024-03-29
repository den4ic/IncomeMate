package com.genesiseternity.incomemate

import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.genesiseternity.incomemate.utils.replaceToRegex
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CurrencyFormat
{
    private lateinit var suffix: String
    private lateinit var currencySymbol: Array<String>

    private lateinit var editTextCurrency: EditText

    private var previousCleanString: String = ""
    private val MAX_LENGTH: Int = 20
    private val MAX_DECIMAL: Int = 2

    private var compositeDisposable: Disposable = CompositeDisposable()

    fun getSuffix(): String = suffix
    fun setSuffix(selectedCurrency: Int) { this.suffix = " " + currencySymbol[selectedCurrency] }
    fun getCurrencySymbol(): Array<String> { return currencySymbol }

    fun initFormatCurrencyEditText(editTextCurrency: EditText, currencySymbol: Array<String>, selectedCurrency: Int)
    {
        this.editTextCurrency = editTextCurrency
        this.currencySymbol = currencySymbol

        suffix = " " + currencySymbol[selectedCurrency]

        compositeDisposable = editTextCurrency.textChanges()
            .map { it.toString() }
            .subscribe {
                afterTextChanged(it)
                beforeTextChanged()
            }
    }

    fun dispose()
    {
        if (!compositeDisposable.isDisposed)
        {
            compositeDisposable.dispose()
        }
    }

    fun initFormatCurrencyEditText(editTextCurrency: EditText)
    {
        this.editTextCurrency = editTextCurrency
        suffix = ""
    }

    fun updateSelectedCurrencyType(selectedCurrency: Int)
    {
        val getTempText: CharSequence = editTextCurrency.text.subSequence(0,
            editTextCurrency.text.length - getSuffix().length)
        setSuffix(selectedCurrency)
        val setTempText: String = getTempText.toString() + getSuffix()

        editTextCurrency.setText(setTempText)
    }

    fun setStringTextFormatted(text: String): String
    {
        val cleanString: String = text.replaceToRegex()
        return if (cleanString.contains(".")) formatDecimalWithoutSuffix(cleanString) else formatIntegerWithoutSuffix(cleanString)
    }

    private fun afterTextChanged(str: String)
    {
        // str = editable.toString()
        if (str.length < suffix.length)
        {
            editTextCurrency.setText(suffix)
            editTextCurrency.setSelection(suffix.length)
            return
        }
        if (str.equals(suffix))
        {
            return
        }

        // [,.] [, ] [\\D, ]
        val cleanString: String = str.replace(suffix, "").replaceToRegex()

        if (cleanString.equals(previousCleanString) || cleanString.isEmpty())
        {
            return
        }
        previousCleanString = cleanString

        val formattedString: String = if (cleanString.contains(".")) formatDecimal(cleanString) else formatInteger(cleanString)

        editTextCurrency.setText(formattedString)
        handleSelection()
    }

    private fun setDecimalFormatSymbols(decimalFormat: DecimalFormat, decimalSeparator: Char)
    {
        val customSymbol: DecimalFormatSymbols = decimalFormat.decimalFormatSymbols
        customSymbol.decimalSeparator = decimalSeparator
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol
    }

    //region FormatedWithoutSuffix
    private fun formatIntegerWithoutSuffix(str: String): String {
        val parsed: BigDecimal = BigDecimal(str)
        val formatter: DecimalFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))

        setDecimalFormatSymbols(formatter,' ')
        return formatter.format(parsed)
    }

    private fun formatDecimalWithoutSuffix(str: String): String {
        if (str.equals(".")) {
            return "."
        }
        val parsed: BigDecimal = BigDecimal(str)

        val formatter: DecimalFormat = DecimalFormat( "#,###." + getDecimal(str),
            DecimalFormatSymbols(Locale.US))
        formatter.setRoundingMode(RoundingMode.DOWN)

        setDecimalFormatSymbols(formatter,'.')
        return formatter.format(parsed)
    }
    //endregion

    private fun formatInteger(str: String): String {
        val parsed: BigDecimal = BigDecimal(str)
        val formatter: DecimalFormat = DecimalFormat("#,###" + suffix, DecimalFormatSymbols(Locale.US))

        setDecimalFormatSymbols(formatter,' ')
        return formatter.format(parsed)
    }

    private fun formatDecimal(str: String): String {
        if (str.equals(".")) {
            return "." + suffix
        }
        val parsed: BigDecimal = BigDecimal(str)

        val formatter: DecimalFormat = DecimalFormat( "#,###." + getDecimal(str) + suffix,
            DecimalFormatSymbols(Locale.US))
        formatter.setRoundingMode(RoundingMode.DOWN)

        setDecimalFormatSymbols(formatter,'.')
        return formatter.format(parsed)
    }

    private fun handleSelection()
    {
        if (editTextCurrency.getText().length - suffix.length <= MAX_LENGTH)
        {
            editTextCurrency.setSelection(editTextCurrency.getText().length - suffix.length)
        }
        else
        {
            editTextCurrency.setSelection(MAX_LENGTH)
        }
    }

    private fun getDecimal(str: String): String {
        val decimalCount: Int = str.length - str.indexOf(".") - 1
        val decimalPattern: StringBuilder = StringBuilder()

        var i = 0
        while (i < decimalCount && i < MAX_DECIMAL)
        {
            i++
            decimalPattern.append("0")
        }
        return decimalPattern.toString()
    }

    private fun beforeTextChanged()
    {
        editTextCurrency.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            val tempTextLength: Int = editTextCurrency.text.length - suffix.length
            val cursorPosition: Int = editTextCurrency.selectionStart

            if ((keyCode == KeyEvent.KEYCODE_DEL && cursorPosition > tempTextLength) ||
                (cursorPosition > tempTextLength && cursorPosition <= editTextCurrency.text.length - 1))
            {
                handleSelection()
                return@OnKeyListener true
            }

            if (keyCode == KeyEvent.KEYCODE_DEL && editTextCurrency.text.toString().startsWith("-") &&
                tempTextLength == 2 && cursorPosition > 1)
            {
                editTextCurrency.setText("0 " + suffix)
                editTextCurrency.setSelection(1)
                return@OnKeyListener true
            }

            if (keyCode == KeyEvent.KEYCODE_MINUS && editTextCurrency.text.length == suffix.length
                && !editTextCurrency.text.toString().startsWith("-"))
            {
                editTextCurrency.setText(suffix)
                editTextCurrency.setSelection(0)
                return@OnKeyListener true
            }

            return@OnKeyListener false

        })
    }
}