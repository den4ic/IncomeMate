package com.genesiseternity.incomemate.wallet

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

class CurrencyActivity : DaggerAppCompatActivity() {
    @Inject lateinit var currencyFormat: dagger.Lazy<CurrencyFormat>
    @Inject lateinit var currencyDetailsDao: CurrencyDetailsDao
    @Inject lateinit var currencyColorDao: CurrencyColorDao

    private lateinit var binding: ActivityCurrencyBinding
    private lateinit var idCurrency: TextView
    private lateinit var editTextCurrency: EditText
    private lateinit var editTextAmountCurrency: EditText
    private lateinit var currencySymbol: Array<String>

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    //private String[] listCurrencies = { "Российский рубль", "Доллар США", "Евро", "Дирхам ОАЭ", "Китайский юань",
    //        "Австралийский доллар", "Британский фунт", "Канадский доллар", "Швейцарский франк", "Японская йена" }

    //private String[] currencySymbol = {"₽", "$", "€", "AED", "¥", "$", "£", "$", "₣", "¥"}
    //currencySymbol = view.getResources().getStringArray(R.array.list_currency_symbol)

    private lateinit var listCurrencies: Array<String>
    private lateinit var btnCurrencyType: Button
    private var selectedCurrency: Int = 0

    private var selectedCardViewId: Int = 0
    //private int imageCategoryIntent, selectedColorIdIntent

    private lateinit var gridListCardView: androidx.gridlayout.widget.GridLayout
    private lateinit var cardView: ArrayList<MaterialCardView>

    private var countColorBtn: Int = 0
    private var btnsColorChange: ArrayList<RadioButton> = ArrayList()
    private lateinit var selectedCradView: View
    private var selectedColor: Int = Color.parseColor("#00b894")
    private var selectedBtnColorId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCurrencyIntent: Int = intent.getIntExtra("idCurrency", 0)
        val editTextCurrencyIntent: String? = intent.getStringExtra("editTextCurrency")
        val editTextAmountCurrencyIntent: String? = intent.getStringExtra("editTextAmountCurrency")
        val defaultCurrencyTypeIntent: Int = intent.getIntExtra("defaultCurrencyType", 0)
        // val imageViewCurrencyOneIntent: Int  = intent.getIntExtra("imageViewCurrencyOne", 0)

        idCurrency = binding.idCurrency
        editTextCurrency = binding.editTextCurrency
        editTextAmountCurrency = binding.editTextAmountCurrency
        editTextAmountCurrency.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)

        currencySymbol = resources.getStringArray(R.array.list_currency_symbol)
        //imageViewCurrencyOne = binding.imageViewCurrencyOne

        idCurrency.text = idCurrencyIntent.toString()
        editTextCurrency.setText(editTextCurrencyIntent)
        //editTextAmountCurrency.setText(editTextAmountCurrencyIntent) // + " " + currencySymbol[0])

        editTextAmountCurrency.setText(editTextAmountCurrencyIntent + " " + currencySymbol[defaultCurrencyTypeIntent])
        //imageViewCurrencyOne.setImageResource(imageViewCurrencyOneIntent)

        listCurrencies = resources.getStringArray(R.array.list_currencies)

        btnCurrencyType = binding.btnCurrency
        gridListCardView = binding.gridListCardView

        selectedCurrency = defaultCurrencyTypeIntent
        btnCurrencyType.text = listCurrencies[selectedCurrency]

        //CalculatorKeyboard keyboard = (CalculatorKeyboard) binding.calculatorKeyboard
        //editTextAmountCurrency.setRawInputType(InputType.TYPE_CLASS_TEXT)
        //editTextAmountCurrency.setTextIsSelectable(true)
        //InputConnection ic = editTextAmountCurrency.onCreateInputConnection(new EditorInfo())
        //keyboard.setInputConnection(ic)
        //Keyboard customKeyboard = new Keyboard(this, R.layout.fragment_settings)


        initInsertDB()
        saveDataCurrency()
        deleteDataCurrency()

    }

    override fun onDestroy() {
        currencyFormat.get().disposableCurrencyEditText()
        super.onDestroy()
    }

    private fun choiceCurrencyType()
    {
        btnCurrencyType.text = listCurrencies[selectedCurrency]

        btnCurrencyType.setOnClickListener()
        {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(it.context)
            alertDialogBuilder.setTitle("Основная валюта")
            alertDialogBuilder.setCancelable(true)

            alertDialogBuilder.setSingleChoiceItems(listCurrencies, selectedCurrency) { dialogInterface, index ->
                selectedCurrency = index
            }

            alertDialogBuilder.setPositiveButton("Готово", DialogInterface.OnClickListener() { dialogInterface, i ->
                btnCurrencyType.text = listCurrencies[selectedCurrency]
                Toast.makeText(it.context, "Выбрана валюта: " + listCurrencies[selectedCurrency], Toast.LENGTH_LONG).show()
                currencyFormat.get().updateSelectedCurrencyType(selectedCurrency)
                dialogInterface.dismiss()
            })

            alertDialogBuilder.setNegativeButton("Отменить", DialogInterface.OnClickListener() { dialogInterface, i ->

            })

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
        val idCurrencyTXT: String = idCurrency.text.toString()
        val editTextCurrencyTXT: String = editTextCurrency.text.toString()
        val editTextAmountCurrencyTXT: String = editTextAmountCurrency.text.toString()

        val id: Int = idCurrencyTXT.toInt()

        currencyDetailsDao.insertCurrencyData(initCurrencyEntity(
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
                choiceCurrencyType()
                currencyFormat.get().initFormatCurrencyEditText(editTextAmountCurrency, currencySymbol, selectedCurrency)

                choiceCardView()
                setColorIconCardView()
            },
            {
                val disposableGetSortedData: Disposable = currencyDetailsDao.getAllSortedCurrencyData()
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
                    })

                compositeDisposable.add(disposableGetSortedData)
            }
        )
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
        val disposableGetColorData: Disposable = currencyColorDao.getAllCurrencyColorData()
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

                    btnsColorChange[it[id].id].setBackgroundTintList(ColorStateList.valueOf(it[id].currentColor))
                },
                {
                }
            )

        compositeDisposable.add(disposableGetColorData)
    }

    private fun setColorIconCardView()
    {
        val img: Drawable = getResources().getDrawable(R.drawable.ic_baseline_check_24)
        val listColorChange: RadioGroup = binding.listColorChange
        countColorBtn = listColorChange.childCount-1
        btnsColorChange = ArrayList(countColorBtn)

        for (i in 0..countColorBtn)
        {
            btnsColorChange.add(listColorChange.getChildAt(i) as RadioButton)

            currencyColorDao.insertCurrencyColorData(
                CurrencyColorEntity(
                i,
                btnsColorChange[i].backgroundTintList!!.defaultColor
                )
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    getColorList(i, img)
                },
                {
                    getColorList(i, img)
                }
            )

            btnsColorChange[i].setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked)
                {
                    //compoundButton.setCompoundDrawables(null, null, img, null)
                    compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null)
                }
                else
                {
                    //compoundButton.setCompoundDrawables(null, null, null, null)
                    compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                }

                if (btnsColorChange[i].isChecked)
                {
                    selectedBtnColorId = i
                }
            }

            btnsColorChange[i].setOnClickListener()
            {
                selectedColor = it.backgroundTintList?.defaultColor!!
                //selectedColor = ((ColorDrawable)view.getBackground()).getColor()

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

    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
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
            currencyColorDao.updateCurrencyColorData(CurrencyColorEntity(
                i,
                btnsColorChange[i].backgroundTintList!!.defaultColor
            ))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {

                },
                {

                }
            )
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
        val idCurrencyTXT: String = idCurrency.text.toString()
        val editTextCurrencyTXT: String = editTextCurrency.text.toString()
        //Button deleteBtnCurrencyData = binding.deleteBtnCurrencyData

        val disposableDeleteBtn: Disposable = binding.deleteBtnCurrencyData.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Удалить счёт - " + editTextCurrencyTXT + " ?")
                builder.setCancelable(true)
                builder.setMessage("Все операции связанные с данным счётом будут безвозвратно удалены.\n\nБаланс других счетов не поменяется.")

                builder.setPositiveButton("Удалить") { dialogInterface, i ->

                currencyDetailsDao.deleteCurrentCurrencyData(Integer.parseInt(idCurrencyTXT))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            switchActivity()
                            dialogInterface.dismiss()
                        },
                        {
                        }
                    )
                }

                builder.setNegativeButton("Отменить") { dialogInterface, i ->
                    dialogInterface.cancel()
                }
                builder.show()
            }

        compositeDisposable.add(disposableDeleteBtn)
    }

    //@Override
    //public void onBackPressed()
    //{
    //    if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
    //        getFragmentManager().popBackStack()
    //    } else {
    //        super.onBackPressed()
    //    }
    //}

    private fun switchActivity()
    {
        //Intent intent = new Intent(this, MainActivity.class)
        //MainActivity.LastFragmentActivity = new WalletFragment()
        //startActivity(intent)

        /////MainActivity.switchFragmentActivity(this, new WalletFragment())
        //this.onBackPressed()
        //finish()

        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idNavigationPage", 0)
        startActivity(intent)
    }

}