package com.genesiseternity.incomemate.pieChart

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.colorPicker.ColorPickerActivity
import com.genesiseternity.incomemate.databinding.ActivityCategoryBinding
import com.genesiseternity.incomemate.room.CurrencyColorDao
import com.genesiseternity.incomemate.room.PieChartCategoriesDao
import com.genesiseternity.incomemate.room.PieChartCategoriesTitleDao
import com.genesiseternity.incomemate.room.entities.CurrencyColorEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import com.jakewharton.rxbinding4.view.clicks
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoryActivity : DaggerAppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var idCategory: TextView
    private lateinit var editTextTitleCategory: EditText
    private lateinit var editTextAmountCategory: EditText
    private lateinit var currencySymbol: Array<String>
    //private DBHelper dbHelper = new DBHelper(this)

    @Inject lateinit var currencyFormat: dagger.Lazy<CurrencyFormat>
    //private CurrencyFormat currencyFormat = new CurrencyFormat()
    private var selectedCurrency: Int = 0

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>

    @Inject lateinit var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>

    @Inject lateinit var currencyColorDao: dagger.Lazy<CurrencyColorDao>

    //@Inject
    //public CurrencyDetailsRepository currencyDetailsRepository

    private var imageCategoryIntent: Int = 0
    private var selectedColorIdIntent: Int = 0
    private var currentIdPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idCategory = binding.idCategory
        editTextTitleCategory = binding.editTextTitleCategory
        editTextAmountCategory = binding.editTextAmountCategory
        currencySymbol = resources.getStringArray(R.array.list_currency_symbol)

        val idCategoryIntent: Int = intent.getIntExtra("idCategory", 0)
        currentIdPage = intent.getIntExtra("idPage", 0)
        val editTextTitleCategoryIntent: String? = getIntent().getStringExtra("titleCategoryName")
        val editTextAmountCategoryIntent: String? = getIntent().getStringExtra("amountCategory")
        //int  = getIntent().getIntExtra("imageCategory", 0)
        //int  = getIntent().getIntExtra("selectedColorId", 0)

        imageCategoryIntent = getIntent().getIntExtra("imageCategory", 0)
        selectedColorIdIntent = getIntent().getIntExtra("selectedColorId", 0)
        val defaultCurrencyTypeIntent: Int  = getIntent().getIntExtra("defaultCurrencyType", 0)
        selectedCurrency = defaultCurrencyTypeIntent



        editTextAmountCategory.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)


        idCategory.setText(idCategoryIntent.toString())
        editTextTitleCategory.setText(editTextTitleCategoryIntent)
        editTextAmountCategory.setText(editTextAmountCategoryIntent + " " + currencySymbol[defaultCurrencyTypeIntent])


        listCurrencies = resources.getStringArray(R.array.list_currencies)
        btnCurrencyType = binding.btnCurrencyCategory
        btnCurrencyType.text = listCurrencies[selectedCurrency]

        initInsertDB()
        //choiceCurrencyType()
        //currencyFormat.get().InitFormatCurrencyEditText(editTextAmountCategory, currencySymbol, selectedCurrency)

        //initChoiceCardView()
        //setColorIconCardView()

        saveDataCurrency()
        deleteDataCurrency()

        /*
        pieChartCategoriesDao.get().deleteAllPieChartCategoriesData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver()
                {
                    @Override
                    public void onSubscribe(@NonNull Disposable d)
                    {
                        Log.d("123", "1 - 1")
                    }

                    @Override
                    public void onComplete()
                    {
                        Log.d("123", "2 - 2")
                    }

                    @Override
                    public void onError(@NonNull Throwable e)
                    {
                        Log.d("123", "3 - 3")
                    }
                })
         */
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        currencyFormat.get().disposableCurrencyEditText()
        super.onDestroy()
    }

    private lateinit var listCurrencies: Array<String>
    private lateinit var btnCurrencyType: Button

    private val alertCurrencyTitle: String = "Основная валюта"
    private val alertCurrencyAccept: String = "Готово"
    private val alertCurrencyCancel: String = "Отменить"

    private fun choiceCurrencyType()
    {
        btnCurrencyType.setText(listCurrencies[selectedCurrency])

        btnCurrencyType.setOnClickListener()
        {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(it.context)
            alertDialogBuilder.setTitle(alertCurrencyTitle)
            alertDialogBuilder.setCancelable(true)

            alertDialogBuilder.setSingleChoiceItems(listCurrencies, selectedCurrency) { dialogInterface, index ->
                selectedCurrency = index
            }


            alertDialogBuilder.setPositiveButton(alertCurrencyAccept) { dialogInterface, i ->
                btnCurrencyType.setText(listCurrencies[selectedCurrency])
                Toast.makeText(it.context, "Выбрана валюта: " + listCurrencies[selectedCurrency], Toast.LENGTH_LONG).show()

                currencyFormat.get().updateSelectedCurrencyType(selectedCurrency)

                dialogInterface.dismiss()
            }

            alertDialogBuilder.setNegativeButton(alertCurrencyCancel) { dialogInterface, i -> }

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.setCanceledOnTouchOutside(true)
                alertDialog.show()
        }
    }

    private fun initInsertDB()
    {
        val idCategoryTXT: String = idCategory.getText().toString()
        val editTextTitleCategoryTXT: String = editTextTitleCategory.getText().toString()
        val editTextAmountCategoryTXT: String = editTextAmountCategory.getText().toString()

        val id: Int = Integer.parseInt(idCategoryTXT)

        selectedCardViewId = Integer.parseInt(idCategoryTXT)
        selectedColor = selectedColorIdIntent



        compositeDisposable.add(pieChartCategoriesDao.get().insertPieChartCategoriesData(initCategoriesEntity(
            idCategoryTXT,
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

    private var countColorBtn: Int = 0
    private lateinit var btnsColorChange: ArrayList<RadioButton>
    private var selectedCardViewId: Int = 0
    private var selectedColor: Int = Color.parseColor("#00b894")
    private var selectedBtnColorId: Int = 0

    private fun getColorList(id: Int, img: Drawable)
    {
        val disposableGetColorData: Disposable = currencyColorDao.get().getAllCurrencyColorData()
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
            )

        compositeDisposable.add(disposableGetColorData)
    }

    private fun setColorIconCardView()
    {
        val img: Drawable = getResources().getDrawable(R.drawable.ic_baseline_check_24)
        val listColorChange: RadioGroup = binding.listColorChangeCategory
        countColorBtn = listColorChange.childCount-1
        btnsColorChange = ArrayList(countColorBtn)

        for (i in 0..countColorBtn)
        {
            val tempIdBtn: Int = i
            btnsColorChange.add(listColorChange.getChildAt(i) as RadioButton)

            compositeDisposable.add(currencyColorDao.get().insertCurrencyColorData(CurrencyColorEntity(
                i,
                btnsColorChange[i].backgroundTintList?.defaultColor!!
            ))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    getColorList(tempIdBtn, img)
                },
                {
                    getColorList(tempIdBtn, img)
                }
            ))

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

                if (btnsColorChange[tempIdBtn].isChecked())
                {
                    selectedBtnColorId = tempIdBtn
                }
            }

            btnsColorChange[i].setOnClickListener()
            {
                selectedColor = it.backgroundTintList?.defaultColor!!
                //selectedColor = ((ColorDrawable)view.getBackground()).getColor()

                iconCategoryCardView.setColorBackgroundCardView(selectedColor)
            }
        }

        createNewColorPicker()
    }


    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 80) {
            val intent: Intent? = it.data
            if (intent != null) {
                selectedColor = intent.getIntExtra("selectedColorPicker", 0)
                btnsColorChange[selectedBtnColorId].setBackgroundTintList(ColorStateList.valueOf(selectedColor))
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


    //public PieChartCategoriesEntity initCategoriesEntity(String id, String titleCategory, String amountCategory, int currencyType, int idIcon, int idColorIcon)
    private fun initCategoriesEntity(id: String, amountCategory: String): PieChartCategoriesEntity
    {
        val currencyDetailsEntity: PieChartCategoriesEntity = PieChartCategoriesEntity(
            id.toInt(),
            currentIdPage,
            amountCategory
        )
        return currencyDetailsEntity
    }

    private fun saveDataCategoryPieChart()
    {
        val idCategoryTXT: String = idCategory.getText().toString()
        val editTextTitleCategoryTXT: String = editTextTitleCategory.getText().toString()

        compositeDisposable.add(pieChartCategoriesTitleDao.get().updatePieChartCategoriesTitleData(PieChartCategoriesTitleEntity(
            idCategoryTXT.toInt(),
            editTextTitleCategoryTXT,
            selectedCurrency,
            iconCategoryCardView.getSelectedCardViewId(),
            selectedColor)
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {},
            {}
        ))
    }

    private fun saveDataColorList()
    {
        for (i in 0..countColorBtn)
        {
            compositeDisposable.add(currencyColorDao.get().updateCurrencyColorData(CurrencyColorEntity(
                i,
                btnsColorChange[i].backgroundTintList?.defaultColor!!
            )
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
                .subscribe(
                    {},
                    {}
                ))
        }
    }

    private fun saveDataCurrency()
    {
        val idCategoryTXT: String = idCategory.getText().toString()

        val disposableSaveBtn: Disposable = binding.saveBtnCategoryData.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //String editTextTitleCategoryTXT = editTextTitleCategory.getText().toString()
                val editTextAmountCategoryTXT: String = editTextAmountCategory.text.toString()

                compositeDisposable.add(pieChartCategoriesDao.get().updatePieChartCategoriesData(
                    initCategoriesEntity(
                        idCategoryTXT,
                        //editTextTitleCategoryTXT,
                        editTextAmountCategoryTXT
                        //selectedCurrency,
                        //iconCategoryCardView.getSelectedCardViewId(),
                        //selectedColor
                    )
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            saveDataCategoryPieChart()
                            saveDataColorList()
                            switchActivity()
                        },
                        {

                        }
                    ))
            }

        compositeDisposable.add(disposableSaveBtn)
    }

    private val alertDeleteTitlePrefix: String = "Удалить категорию - "
    private val alertDeleteTitleSuffix: String = " ?"
    private val alertDeleteSetMessage: String = "Все операции связанные с данной категорией будут безвозвратно удалены."
    private val alertDeleteAccept: String = "Удалить"
    private val alertDeleteCancel: String = "Отменить"

    private fun deleteDataCurrency()
    {
        val idCategoryTXT: String = idCategory.text.toString()
        val editTextTitleCategoryTXT: String = editTextTitleCategory.text.toString()

        val disposableDeleteBtn: Disposable = binding.deleteBtnCategoryData.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle(alertDeleteTitlePrefix + editTextTitleCategoryTXT + alertDeleteTitleSuffix)
                builder.setCancelable(true)

                builder.setMessage(alertDeleteSetMessage)

                builder.setPositiveButton(alertDeleteAccept) { dialogInterface, i ->
                    compositeDisposable.add(pieChartCategoriesDao.get().deleteCurrentPieChartCategoriesData(java.lang.Integer.parseInt(idCategoryTXT))
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

                builder.setNegativeButton(alertDeleteCancel) { dialogInterface, i ->
                    dialogInterface.cancel()
                }
                builder.show()
            }

        compositeDisposable.add(disposableDeleteBtn)
    }

    private fun switchActivity()
    {
        //MainActivity.switchFragmentActivity(this, new PieChartFragment())

        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idNavigationPage", 1)
        startActivity(intent)
    }

}