package com.genesiseternity.incomemate.pieChart

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.colorPicker.ColorPickerActivity
import com.genesiseternity.incomemate.databinding.ActivityCategoryBinding
import com.genesiseternity.incomemate.room.*
import com.genesiseternity.incomemate.room.entities.CurrencyColorEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import com.genesiseternity.incomemate.utils.replaceToRegex
import com.jakewharton.rxbinding4.view.clicks
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoryActivity : DaggerAppCompatActivity()
{
    @Inject lateinit var currencyFormat: dagger.Lazy<CurrencyFormat>
    @Inject lateinit var currencyDetailsDao: dagger.Lazy<CurrencyDetailsDao>
    @Inject lateinit var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>
    @Inject lateinit var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>
    @Inject lateinit var currencyColorDao: dagger.Lazy<CurrencyColorDao>
    @Inject lateinit var currencySettingsDao: dagger.Lazy<CurrencySettingsDao>

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var idCategory: TextView
    private lateinit var editTextTitleCategory: EditText
    private lateinit var editTextAmountCategory: EditText
    private lateinit var currencySymbol: Array<String>

    private var selectedCurrency: Int = 0

    private var imageCategoryIntent: Int = 0
    private var selectedColorIdIntent: Int = 0
    private var defaultCurrencyTypeIntent: Int = 0
    private var currentIdPage: Int = 0
    private var idCurrencyAccount: Int = 0

    private lateinit var listCurrencies: Array<String>
    private lateinit var imageCategoryType: TypedArray
    private lateinit var btnCurrencyType: Button

    private var countColorBtn: Int = 0
    private lateinit var btnsColorChange: ArrayList<RadioButton>
    private var selectedCardViewId: Int = 0
    private var selectedColor: Int = Color.parseColor("#00b894")
    private var selectedBtnColorId: Int = 0

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val alertCurrencyTitle: String = "Основная валюта"
    private val alertCurrencyAccept: String = "Готово"
    private val alertCurrencyToast: String = "Выбрана валюта: "
    private val alertCurrencyCancel: String = "Отменить"

    private val alertDeleteTitlePrefix: String = "Удалить категорию - "
    private val alertDeleteTitleSuffix: String = " ?"
    private val alertDeleteSetMessage: String = "Все операции связанные с данной категорией будут безвозвратно удалены."
    private val alertDeleteAccept: String = "Удалить"
    private val alertDeleteCancel: String = "Отменить"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idCategory = binding.idCategory
        editTextTitleCategory = binding.editTextTitleCategory
        editTextAmountCategory = binding.editTextAmountCategory
        currencySymbol = resources.getStringArray(R.array.list_currency_symbol)
        listCurrencies = resources.getStringArray(R.array.list_currencies)
        imageCategoryType = resources.obtainTypedArray(R.array.image_category_type)

        val idCategoryIntent: Int = intent.getIntExtra("idCategory", 0)
        val editTextTitleCategoryIntent: String? = intent.getStringExtra("titleCategoryName")
        val editTextAmountCategoryIntent: String? = intent.getStringExtra("amountCategory")
        currentIdPage = intent.getIntExtra("idPage", 0)
        imageCategoryIntent = intent.getIntExtra("imageCategory", 0)
        selectedColorIdIntent = intent.getIntExtra("selectedColorId", 0)
        defaultCurrencyTypeIntent = intent.getIntExtra("defaultCurrencyType", 0)
        selectedCurrency = defaultCurrencyTypeIntent

        editTextAmountCategory.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED

        idCategory.text = idCategoryIntent.toString()
        editTextTitleCategory.setText(editTextTitleCategoryIntent)
        editTextAmountCategory.setText(editTextAmountCategoryIntent + " " + currencySymbol[defaultCurrencyTypeIntent])


        btnCurrencyType = binding.btnCurrencyCategory
        btnCurrencyType.text = listCurrencies[selectedCurrency]

        initialize()

        compositeDisposable.add(currencySettingsDao.get().getDefaultIdCurrencyAccountByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { idCurrencyAccount = it }, {} ))


        saveDataCurrency()
        deleteDataCurrency()
    }

    override fun onDestroy()
    {
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
            alertDialogBuilder.setTitle(alertCurrencyTitle)
            alertDialogBuilder.setCancelable(true)

            alertDialogBuilder.setSingleChoiceItems(listCurrencies, selectedCurrency) { dialogInterface, index ->
                selectedCurrency = index
            }

            alertDialogBuilder.setPositiveButton(alertCurrencyAccept) { dialogInterface, i ->
                btnCurrencyType.text = listCurrencies[selectedCurrency]
                Toast.makeText(it.context, alertCurrencyToast + listCurrencies[selectedCurrency], Toast.LENGTH_LONG).show()

                currencyFormat.get().updateSelectedCurrencyType(selectedCurrency)

                dialogInterface.dismiss()
            }

            alertDialogBuilder.setNegativeButton(alertCurrencyCancel) { dialogInterface, i -> }

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.setCanceledOnTouchOutside(true)
                alertDialog.show()
        }
    }

    private fun initialize()
    {
        val idCategory: String = idCategory.text.toString()
        val editTextAmountCategoryTXT: String = editTextAmountCategory.text.toString()
        val id: Int = idCategory.toInt()

        selectedCardViewId = imageCategoryIntent
        selectedColor = selectedColorIdIntent

        compositeDisposable.add(pieChartCategoriesDao.get().insertPieChartCategoriesData(initCategoriesEntity(
            idCategory,
            editTextAmountCategoryTXT
            ))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    currencyFormat.get().initFormatCurrencyEditText(editTextAmountCategory, currencySymbol, selectedCurrency)
                    choiceCurrencyType()

                    initChoiceCardView()
                    setColorIconCardView()
                },
                {
                    //Disposable disposableGetSortedData = pieChartCategoriesDao.get().getAllSortedPieChartCategoriesData()
                    compositeDisposable.add(pieChartCategoriesDao.get().getPieChartCategoryByIdPage(currentIdPage)
                        .subscribeOn(Schedulers.io())
                        .map {
                            it.filter { it.id == id }
                        }
                        .flatMapObservable { t -> Observable.fromIterable(t) }
                        //.flatMapIterable { it }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                editTextAmountCategory.setText(it.amountCategory)
                                currencyFormat.get().initFormatCurrencyEditText(editTextAmountCategory, currencySymbol, selectedCurrency)
                                choiceCurrencyType()

                                initChoiceCardView()
                                setColorIconCardView()
                            },
                            {
                            }
                        ))
                }
            ))
    }


    private lateinit var iconCategoryCardView: IconCategoryCardView

    private fun initChoiceCardView()
    {
        iconCategoryCardView = IconCategoryCardView(
            this,
            binding.gridViewCategoryIcon,
            selectedColor,
            selectedCardViewId
        )
    }

    private fun getColorList(id: Int, img: Drawable)
    {
        compositeDisposable.add(currencyColorDao.get().getAllCurrencyColorData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    currencyColorEntities ->

                    if (currencyColorEntities[id].currentColor == selectedColor)
                    {
                        btnsColorChange[id].isChecked = true
                        btnsColorChange[id].setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null)
                        selectedBtnColorId = id
                    }

                    btnsColorChange[currencyColorEntities[id].id].backgroundTintList = ColorStateList.valueOf(currencyColorEntities[id].currentColor)
                },
                {
                }
            ))
    }

    private fun setColorIconCardView()
    {
        if (selectedColor == 0)
            selectedColor = ContextCompat.getColor(this, R.color.green)

        val img: Drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_24, null)!!
        val listColorChange: RadioGroup = binding.listColorChangeCategory
        countColorBtn = listColorChange.childCount-1
        btnsColorChange = ArrayList(countColorBtn)

        for (i in 0..countColorBtn)
        {
            btnsColorChange.add(listColorChange.getChildAt(i) as RadioButton)

            compositeDisposable.add(currencyColorDao.get().insertCurrencyColorData(CurrencyColorEntity(
                    i,
                    btnsColorChange[i].backgroundTintList?.defaultColor!!
                ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        getColorList(i, img)
                    },
                    {
                        getColorList(i, img)
                    }
                ))

            btnsColorChange[i].setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked)
                {
                    compoundButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null)
                }
                else
                {
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
                iconCategoryCardView.setColorBackgroundCardView(selectedColor)
            }
        }

        createNewColorPicker()
    }

    private val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 80) {
            val intent: Intent? = it.data
            if (intent != null) {
                selectedColor = intent.getIntExtra("selectedColorPicker", 0)
                btnsColorChange[selectedBtnColorId].backgroundTintList = ColorStateList.valueOf(selectedColor)
                iconCategoryCardView.setColorBackgroundCardView(selectedColor)
            }
        }
    }

    private fun createNewColorPicker()
    {
        binding.addNewColorPickerCategory.setOnClickListener()
        {
            val intent: Intent = Intent(it.context, ColorPickerActivity::class.java)
            intent.putExtra("selectedColorPicker", selectedColor)
            resultLauncher.launch(intent)
        }
    }

    private fun initCategoriesEntity(id: String, amountCategory: String): PieChartCategoriesEntity {
        return PieChartCategoriesEntity(
            id.toInt(),
            currentIdPage,
            amountCategory,
            idCurrencyAccount
        )
    }

    private fun saveDataCategoryPieChart()
    {
        val idCategory = idCategory.text.toString().toInt()
        val editTextTitleCategory = editTextTitleCategory.text.toString()

        compositeDisposable.add(pieChartCategoriesTitleDao.get().updatePieChartCategoriesTitleData(PieChartCategoriesTitleEntity(
            idCategory,
            editTextTitleCategory,
            selectedCurrency,
            iconCategoryCardView.getSelectedCardViewId(),
            selectedColor)
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe( {}, {} ))
    }

    private fun saveDataColorList()
    {
        for (i in 0..countColorBtn)
        {
            compositeDisposable.add(currencyColorDao.get().updateCurrencyColorData(CurrencyColorEntity(
                i,
                btnsColorChange[i].backgroundTintList?.defaultColor!!
            ))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe( {}, {} ))
        }
    }

    private fun saveDataCurrency()
    {
        val idCategory: String = idCategory.text.toString()

        compositeDisposable.add(binding.saveBtnCategoryData.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val editTextAmountCategory: String = editTextAmountCategory.text.toString()

                compositeDisposable.add(pieChartCategoriesDao.get().updatePieChartCategoriesData(
                    initCategoriesEntity(
                        idCategory,
                        editTextAmountCategory
                    ))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            saveDataCategoryPieChart()
                            saveDataColorList()
                            updateAllAccount()
                            switchActivity()
                        },
                        {
                            it.printStackTrace()
                        }
                    ))
            })
    }

    private fun deleteDataCurrency()
    {
        val idCategory: Int = idCategory.text.toString().toInt()
        val editTextTitleCategory: String = editTextTitleCategory.text.toString()

        compositeDisposable.add(binding.deleteBtnCategoryData.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle(alertDeleteTitlePrefix + editTextTitleCategory + alertDeleteTitleSuffix)
                builder.setCancelable(true)

                builder.setMessage(alertDeleteSetMessage)

                builder.setPositiveButton(alertDeleteAccept) { dialogInterface, i ->

                    compositeDisposable.add(pieChartCategoriesDao.get().deleteCurrentPieChartCategoriesData(idCategory)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                compositeDisposable.add(pieChartCategoriesTitleDao.get().deleteCurrentPieChartCategoriesTitleData(idCategory)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        {
                                            switchActivity()
                                            dialogInterface.dismiss()
                                        },
                                        {
                                            it.printStackTrace()
                                            dialogInterface.dismiss()
                                        }
                                    ))
                            },
                            {
                                it.printStackTrace()
                                dialogInterface.dismiss()
                            }
                        ))
                }

                builder.setNegativeButton(alertDeleteCancel) { dialogInterface, i ->
                    dialogInterface.cancel()
                }
                builder.show()
            })
    }

    private fun switchActivity()
    {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idNavigationPage", 1)
        startActivity(intent)
    }

    private fun updateAllAccount()
    {
        compositeDisposable.add(currencyDetailsDao.get().getAmountCurrencyById(idCurrencyAccount)
            .filter {
                return@filter it.isNotEmpty()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    /*
                    var amountExpenses: Float = 0f
                    for (i in it.indices)
                    {
                        val tempAmountCurrency: String = it[i].amountCategory.replaceToRegex()
                        amountExpenses += tempAmountCurrency.toFloat()
                        //Log.d("PieChartFragment", " = " + tempAmountCurrency + " " + it[i].idCurrencyAccount)
                    }
                     */

                    val amountCategory: String = editTextAmountCategory.text.toString().replaceToRegex()
                    val amountCurrency: String = it[0].replaceToRegex()
                    val res: Float = amountCurrency.toFloat() - amountCategory.toFloat()

                    // Spend to card = 0 id
                    compositeDisposable.add(currencyDetailsDao.get().updateCurrentAccount(
                        idCurrencyAccount,
                        currencyFormat.get().setStringTextFormatted(res.toString() + " " +  currencySymbol[defaultCurrencyTypeIntent])
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                            },
                            {
                                it.printStackTrace()
                            }
                        ))
                },
                {
                    it.printStackTrace()
                }
            ))
    }
}