package com.genesiseternity.incomemate

import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.Disposable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CurrencyFormat {
    private lateinit var suffix: String
    private lateinit var currencySymbol: Array<String>

    fun getSuffix(): String { return suffix }
    fun setSuffix(selectedCurrency: Int) { this.suffix = " " + currencySymbol[selectedCurrency] }
    fun getCurrencySymbol(): Array<String> { return currencySymbol }

    private lateinit var editTextCurrency: EditText
    //public EditText getEditTextCurrency() { return editTextCurrency }
    //public void setEditTextCurrency(EditText editTextCurrency) { this.editTextCurrency = editTextCurrency }

    private var previousCleanString: String = ""
    private val MAX_LENGTH: Int = 20
    private val MAX_DECIMAL: Int = 2

    private lateinit var disposableTextChanges: Disposable

    fun initFormatCurrencyEditText(editTextCurrency: EditText, currencySymbol: Array<String>, selectedCurrency: Int)
    {
        this.editTextCurrency = editTextCurrency
        this.currencySymbol = currencySymbol

        suffix = " " + currencySymbol[selectedCurrency]
        //editTextCurrency.addTextChangedListener(this)

        disposableTextChanges = editTextCurrency.textChanges()
            .map { it.toString() }
            .subscribe {
                afterTextChanged(it)
                beforeTextChanged()
            }
    }

    fun disposableCurrencyEditText()
    {
        if (!disposableTextChanges.isDisposed)
        {
            disposableTextChanges.dispose()
        }
    }

    fun initFormatCurrencyEditText(editTextCurrency: EditText)
    {
        this.editTextCurrency = editTextCurrency

        suffix = ""
        //editTextCurrency.addTextChangedListener(this)
    }

    //public void SetFormatFirstInit(int selectedCurrency)
    //{
    //    String suffix = "0 " + currencySymbol[selectedCurrency]
    //    editTextCurrency.removeTextChangedListener(this)
    //    editTextCurrency.setText(suffix)
    //    editTextCurrency.addTextChangedListener(this)
    //}

    fun updateSelectedCurrencyType(selectedCurrency: Int)
    {
        val getTempText: CharSequence = editTextCurrency.getText().subSequence(0,
            editTextCurrency.text.length - getSuffix().length)
        setSuffix(selectedCurrency)
        val setTempText: String = getTempText.toString() + getSuffix()

        //editTextCurrency.removeTextChangedListener(this)
        editTextCurrency.setText(setTempText)
        //editTextCurrency.addTextChangedListener(this)
    }

    //public String SetStringTextFormated(String text, boolean formatIsNegative)
    fun setStringTextFormatted(text: String): String
    {
        //if (text == "0")
        //    return text

        //String cleanString = text.replaceAll(formatIsNegative ? "[^\\d.-]" : "[^\\d.]", "")
        val cleanString: String = text.replace("[^\\d.-]".toRegex(), "")
        return if (cleanString.contains(".")) formatDecimalWithoutSuffix(cleanString) else formatIntegerWithoutSuffix(cleanString)
    }

    fun afterTextChanged(str: String)
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

        //String cleanString = str.replace(suffix, "").replaceAll("[,.]", "")
        //String cleanString = str.replace(suffix, "").replaceAll("[, ]", "")
        //String cleanString = str.replace(suffix, "").replaceAll("[\\D, ]", "")
        val cleanString: String = str.replace(suffix, "").replace("[^\\d.-]".toRegex(), "")

        if (cleanString.equals(previousCleanString) || cleanString.isEmpty())
        {
            return
        }
        previousCleanString = cleanString

        val formattedString: String = if (cleanString.contains(".")) formatDecimal(cleanString) else formatInteger(cleanString)

        //editTextCurrency.removeTextChangedListener(this) // Remove listener
        editTextCurrency.setText(formattedString)
        handleSelection()
        //editTextCurrency.addTextChangedListener(this) // Add listener
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

    fun beforeTextChanged()
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