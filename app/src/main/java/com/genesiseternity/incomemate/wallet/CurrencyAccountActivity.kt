package com.genesiseternity.incomemate.wallet

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.colorPicker.ColorPickerActivity
import com.genesiseternity.incomemate.databinding.ActivityCurrencyBinding
import com.genesiseternity.incomemate.room.CurrencyColorDao
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.entities.CurrencyColorEntity
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import com.google.android.material.card.MaterialCardView
import com.jakewharton.rxbinding4.view.clicks
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrencyAccountActivity : DaggerAppCompatActivity()
{
    @Inject lateinit var currencyFormat: dagger.Lazy<CurrencyFormat>
    @Inject lateinit var currencyDetailsDao: CurrencyDetailsDao
    @Inject lateinit var currencyColorDao: CurrencyColorDao

    private lateinit var binding: ActivityCurrencyBinding
    private lateinit var idCurrency: TextView
    private lateinit var editTextCurrency: EditText
    private lateinit var editTextAmountCurrency: EditText
    private lateinit var currencySymbol: Array<String>

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var listCurrencies: Array<String>
    private lateinit var btnCurrencyType: Button
    private var selectedCurrency: Int = 0

    private var selectedCardViewId: Int = 0

    private lateinit var gridListCardView: androidx.gridlayout.widget.GridLayout
    private lateinit var cardView: ArrayList<MaterialCardView>

    private var countColorBtn: Int = 0
    private var btnsColorChange: ArrayList<RadioButton> = ArrayList()
    private lateinit var selectedCradView: View
    private var selectedColor: Int = Color.parseColor("#00b894")
    private var selectedBtnColorId: Int = 0

    private val textChoiceCurrBuilderTitle: String = "Основная валюта"
    private val textChoiceCurrBuilderPositive: String = "Готово"
    private val textChoiceCurrToast: String = "Выбрана валюта: "
    private val textChoiceCurrBuilderNegative: String = "Отменить"

    private val textDeleteDataCurrBuilderTitle: String = "Удалить счёт - "
    private val textDeleteDataCurrBuilderTitleSuffix: String = " ?"
    private val textDeleteDataCurrBuilderMessage: String = "Все операции связанные с данным счётом будут безвозвратно удалены.\n\nБаланс других счетов не поменяется."
    private val textDeleteDataCurrBuilderPositive: String = "Удалить"
    private val textDeleteDataCurrBuilderNegative: String = "Отменить"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCurrencyIntent: Int = intent.getIntExtra("idCurrency", 0)
        val editTextCurrencyIntent: String? = intent.getStringExtra("editTextCurrency")
        val editTextAmountCurrencyIntent: String? = intent.getStringExtra("editTextAmountCurrency")
        val defaultCurrencyTypeIntent: Int = intent.getIntExtra("defaultCurrencyType", 0)

        idCurrency = binding.idCurrency
        editTextCurrency = binding.editTextCurrency
        editTextAmountCurrency = binding.editTextAmountCurrency
        editTextAmountCurrency.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)

        currencySymbol = resources.getStringArray(R.array.list_currency_symbol)

        idCurrency.text = idCurrencyIntent.toString()
        editTextCurrency.setText(editTextCurrencyIntent)
        editTextAmountCurrency.setText(editTextAmountCurrencyIntent + " " + currencySymbol[defaultCurrencyTypeIntent])
        listCurrencies = resources.getStringArray(R.array.list_currencies)

        btnCurrencyType = binding.btnCurrency
        gridListCardView = binding.gridListCardView

        selectedCurrency = defaultCurrencyTypeIntent
        btnCurrencyType.text = listCurrencies[selectedCurrency]

        initInsertDB()
        saveDataCurrency()
        deleteDataCurrency()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        currencyFormat.get().dispose()
        super.onDestroy()
    }

    private fun choiceCurrencyType()
    {
        btnCurrencyType.text = listCurrencies[selectedCurrency]

        btnCurrencyType.setOnClickListener()
        {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(it.context)
            alertDialogBuilder.setTitle(textChoiceCurrBuilderTitle)
            alertDialogBuilder.setCancelable(true)

            alertDialogBuilder.setSingleChoiceItems(listCurrencies, selectedCurrency) { dialogInterface, index ->
                selectedCurrency = index
            }

            alertDialogBuilder.setPositiveButton(textChoiceCurrBuilderPositive) { dialogInterface, i ->
                btnCurrencyType.text = listCurrencies[selectedCurrency]
                Toast.makeText(it.context, textChoiceCurrToast + listCurrencies[selectedCurrency], Toast.LENGTH_LONG).show()
                currencyFormat.get().updateSelectedCurrencyType(selectedCurrency)
                dialogInterface.dismiss()
            }

            alertDialogBuilder.setNegativeButton(textChoiceCurrBuilderNegative) { dialogInterface, i -> }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.show()
        }
    }

    private fun initCurrencyEntity(
        id: String,
        titleCurrency: String,
        amountCurrency: String,
        currencyType: Int,
        idIcon: Int,
        idColorIcon: Int
    ): CurrencyDetailsEntity {
        return CurrencyDetailsEntity(
            id.toInt(),
            titleCurrency,
            amountCurrency,
            currencyType,
            idIcon,
            idColorIcon
        )
    }

    private fun initInsertDB()
    {
        val idCurrency: String = idCurrency.text.toString()
        val editTextCurrencyText: String = editTextCurrency.text.toString()
        val editTextAmountCurrencyText: String = editTextAmountCurrency.text.toString()
        val id: Int = idCurrency.toInt()

        compositeDisposable.add(currencyDetailsDao.insertCurrencyData(initCurrencyEntity(
            idCurrency,
            editTextCurrencyText,
            editTextAmountCurrencyText,
            selectedCurrency,
            selectedCardViewId,
            selectedColor
        ))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(
            {
                choiceCurrencyType()
                currencyFormat.get().initFormatCurrencyEditText(editTextAmountCurrency, currencySymbol, selectedCurrency)

                choiceCardView()
                setColorIconCardView()
            },
            {
                compositeDisposable.add(currencyDetailsDao.getAllSortedCurrencyData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                    {
                        for (i in it.indices)
                        {
                            if (it[i].id == id)
                            {
                                editTextCurrency.setText(it[i].titleCurrency)
                                editTextAmountCurrency.setText(it[i].amountCurrency)
                                selectedCurrency = it[i].currencyType
                                selectedCardViewId = it[i].idCardViewIcon
                                selectedColor = it[i].idColorIcon

                                choiceCurrencyType()
                                currencyFormat.get().initFormatCurrencyEditText(editTextAmountCurrency, currencySymbol, selectedCurrency)

                                choiceCardView()
                                setColorIconCardView()
                            }
                        }
                    },
                    {
                    }))
            }
        ))
    }

    private fun initChoiceFirstCardView()
    {
        if (selectedColor == 0)
            selectedColor = Color.parseColor("#00b894")

        val countCardView: Int = gridListCardView.childCount-1
        cardView = ArrayList(countCardView)

        for (i in 0..countCardView)
        {
            cardView.add(gridListCardView.getChildAt(i) as MaterialCardView)

            if (selectedCardViewId == i)
            {
                cardView[i].isChecked = true
                cardView[i].isClickable = false
                cardView[i].setCardBackgroundColor(selectedColor)
                cardView[i].toggle()

                selectedCradView = cardView[i]
            }
        }
    }

    private fun choiceCardView()
    {
        initChoiceFirstCardView()

        for (j in cardView.indices)
        {
            cardView[j].setOnClickListener {
                for (i in cardView.indices) {
                    if (it == cardView[i]) {
                        if (cardView[i].isClickable) {
                            selectedCradView = it
                            selectedCardViewId = i

                            cardView[i].setCardBackgroundColor(selectedColor)
                            cardView[i].toggle()
                        }
                        cardView[i].isClickable = false
                    } else // if (!cardView[i].isClickable())
                    {
                        cardView[i].setCardBackgroundColor(Color.WHITE)
                        cardView[i].isChecked = false
                        cardView[i].isClickable = true
                    }
                }
            }
        }
    }

    private fun setColorBackgroundCardView()
    {
        for (i in cardView.indices)
        {
            if (selectedCradView == cardView[i])
            {
                selectedCardViewId = i
                cardView[i].setCardBackgroundColor(selectedColor)
            }
        }
    }

    private fun getColorList(id: Int, img: Drawable)
    {
        compositeDisposable.add(currencyColorDao.getAllCurrencyColorData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    if (it[id].currentColor == selectedColor)
                    {
                        btnsColorChange[id].isChecked = true
                        btnsColorChange[id].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null)
                        selectedBtnColorId = id
                    }

                    btnsColorChange[it[id].id].backgroundTintList = ColorStateList.valueOf(it[id].currentColor)
                },
                {
                }
            ))
    }

    private fun setColorIconCardView()
    {
        val img: Drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_24, null)!!
        val listColorChange: RadioGroup = binding.listColorChange
        countColorBtn = listColorChange.childCount-1
        btnsColorChange = ArrayList(countColorBtn)

        for (i in 0..countColorBtn)
        {
            btnsColorChange.add(listColorChange.getChildAt(i) as RadioButton)

            compositeDisposable.add(currencyColorDao.insertCurrencyColorData(CurrencyColorEntity(
                i,
                btnsColorChange[i].backgroundTintList!!.defaultColor
            ))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    getColorList(i, img)
                },
                {
                    getColorList(i, img)
                }
            ))

            btnsColorChange[i].setOnCheckedChangeListener { compoundButton, isChecked ->

                //compoundButton.setCompoundDrawables(null, null, img, null)
                compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, if (isChecked) img else null, null)

                if (btnsColorChange[i].isChecked)
                {
                    selectedBtnColorId = i
                }
            }

            btnsColorChange[i].setOnClickListener()
            {
                selectedColor = it.backgroundTintList?.defaultColor!!
                setColorBackgroundCardView()
            }
        }

        createNewColorPicker()
    }

    private fun createNewColorPicker()
    {
        binding.addNewColorPicker.setOnClickListener()
        {
            val intent: Intent = Intent(it.context, ColorPickerActivity::class.java)
            intent.putExtra("selectedColorPicker", selectedColor)
            resultLauncher.launch(intent)
        }
    }

    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        if (it.resultCode == 80) {
            val intent: Intent? = it.data
            if (intent != null) {
                selectedColor = intent.getIntExtra("selectedColorPicker", 0)

                btnsColorChange[selectedBtnColorId].backgroundTintList = ColorStateList.valueOf(selectedColor)

                setColorBackgroundCardView()
            }
        }
    }

    private fun saveDataColorList()
    {
        for (i in 0..countColorBtn)
        {
            compositeDisposable.add(currencyColorDao.updateCurrencyColorData(CurrencyColorEntity(
                i,
                btnsColorChange[i].backgroundTintList!!.defaultColor
            ))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {}, {} ))
        }
    }

    private fun saveDataCurrency()
    {
        val idCurrencyTXT: String = idCurrency.text.toString()

        val disposableSaveBtn: Disposable = binding.saveBtnCurrencyData.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val editTextCurrencyTXT: String = editTextCurrency.text.toString()
                val editTextAmountCurrencyTXT: String = editTextAmountCurrency.text.toString()

                compositeDisposable.add(currencyDetailsDao.updateCurrencyData(initCurrencyEntity(
                    idCurrencyTXT,
                    editTextCurrencyTXT,
                    editTextAmountCurrencyTXT,
                    selectedCurrency,
                    selectedCardViewId,
                    selectedColor
                ))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        saveDataColorList()
                        switchActivity()
                    },
                    {
                    }
                ))
            }

        compositeDisposable.add(disposableSaveBtn)
    }

    private fun deleteDataCurrency()
    {
        val idCurrency: Int = idCurrency.text.toString().toInt()
        val editTextCurrency: String = editTextCurrency.text.toString()

        compositeDisposable.add(binding.deleteBtnCurrencyData.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle(textDeleteDataCurrBuilderTitle + editTextCurrency + textDeleteDataCurrBuilderTitleSuffix)
                builder.setCancelable(true)
                builder.setMessage(textDeleteDataCurrBuilderMessage)

                builder.setPositiveButton(textDeleteDataCurrBuilderPositive) { dialogInterface, i ->

                    compositeDisposable.add(currencyDetailsDao.deleteCurrentCurrencyData(idCurrency)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            {
                                switchActivity()
                                dialogInterface.dismiss()
                            },
                            {
                            }
                        ))
                }

                builder.setNegativeButton(textDeleteDataCurrBuilderNegative) { dialogInterface, i ->
                    dialogInterface.cancel()
                }
                builder.show()
            })
    }

    private fun switchActivity()
    {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idNavigationPage", 0)
        startActivity(intent)
    }

}