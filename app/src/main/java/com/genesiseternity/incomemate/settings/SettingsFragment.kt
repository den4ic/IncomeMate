package com.genesiseternity.incomemate.settings

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.helper.widget.Carousel.Adapter
import androidx.constraintlayout.helper.widget.Carousel.TEXT_ALIGNMENT_CENTER
import androidx.core.content.FileProvider
import androidx.core.graphics.green
import androidx.core.view.allViews
import androidx.core.view.get
import androidx.core.view.indices
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.auth.LoginActivity
import com.genesiseternity.incomemate.databinding.FragmentSettingsBinding
import com.genesiseternity.incomemate.history.HistoryRecyclerModel
import com.genesiseternity.incomemate.room.*
import com.genesiseternity.incomemate.room.entities.CurrencySettingsEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import com.genesiseternity.incomemate.utils.LanguageConfig
import com.genesiseternity.incomemate.utils.replaceToRegex
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.time.Duration.Companion.seconds

class SettingsFragment : DaggerFragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject lateinit var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>
    @Inject lateinit var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>
    @Inject lateinit var pieChartDao: dagger.Lazy<PieChartDao>
    @Inject lateinit var currencyColorDao: dagger.Lazy<CurrencyColorDao>
    @Inject lateinit var currencyDetailsDao: dagger.Lazy<CurrencyDetailsDao>
    @Inject lateinit var currencySettingsDao: dagger.Lazy<CurrencySettingsDao>

    @Inject lateinit var languageConfig: dagger.Lazy<LanguageConfig>

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val alertRemoveDataTitle: String = "Удалить данные"
    private val alertRemoveDataMessage: String = "Все ваши данные (счета, категории, история операций) будут безвозвратно удалены."
    private val alertRemoveDataPositive: String = "Удалить все данные"

    private val textBtnMainCurrency: String = "Основная валюта:\n"
    private val alertChoiceCurrTitle: String = "Основная валюта"
    private val alertChoiceCurrToast: String = "Выбрана валюта: "

    private val textLanguageBtn: String = "Язык: "
    private val alertTranslateTitle: String = "Основной язык"
    private val alertTranslateToast: String = "Выбран язык: "

    private val alertDialogPositive: String = "Готово"
    private val alertDialogNegative: String = "Отменить"

    private val titleCsvFileName: String = "History of expense & income transactions "
    private val appendStrBuilderCsv: String = "Time,Category,Amount"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view: View = binding.root

        singleChoiceCurrencyItems()

        binding.removeAllData.setOnClickListener()
        {
            removeAllDataSettings()
        }

        translateLanguage()

        binding.passcodeBtn.setOnClickListener {
            openPagePasscode(view)
        }

        binding.exportCSV.setOnClickListener {
            exportToCsvFile(view)
        }

        binding.settingsPrivacyPolicy.setOnClickListener {
            openPrivacyPolicy()
        }

        return view
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun removeAllDataSettings()
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(alertRemoveDataTitle)
        builder.setCancelable(true)
        builder.setMessage(alertRemoveDataMessage)

        builder.setPositiveButton(alertRemoveDataPositive) { dialogInterface, i ->
            currencySettingsDao.get().deleteAllCurrencySettingsData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("SettingsFragment", "SettingsFragment - deleteAllCurrencySettingsData")
                    },
                    {
                    }
                )
            currencyDetailsDao.get().deleteAllCurrencyData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("SettingsFragment", "SettingsFragment - deleteAllCurrencyData")
                    },
                    {
                    }
                )
            currencyColorDao.get().deleteAllCurrencyColorData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("SettingsFragment", "SettingsFragment - deleteAllCurrencyColorData")
                    },
                    {
                    }
                )
            pieChartDao.get().deleteAllPieChartData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("SettingsFragment", "SettingsFragment - deleteAllPieChartData")
                    },
                    {
                    }
                )
            pieChartCategoriesDao.get().deleteAllPieChartCategoriesData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("SettingsFragment", "SettingsFragment - deleteAllPieChartCategoriesData")
                    },
                    {
                    }
                )
            pieChartCategoriesTitleDao.get().deleteAllPieChartCategoriesTitleData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("SettingsFragment", "SettingsFragment - deleteAllPieChartCategoriesTitleData")
                    },
                    {
                    }
                )
        }

        builder.setNegativeButton(alertDialogNegative) { dialogInterface, i ->
            dialogInterface.cancel()
        }
        builder.show()
    }


    //region Currency List
    private lateinit var listCurrencies: Array<String>
    private var selectedCurrency: Int = 0

    private fun singleChoiceCurrencyItems()
    {
        compositeDisposable.add(currencySettingsDao.get().getDefaultCurrencyByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    selectedCurrency = it

                    listCurrencies = resources.getStringArray(R.array.list_currencies)
                    val btnMainCurrency: Button = binding.BtnMainCurrency
                    btnMainCurrency.text = textBtnMainCurrency + listCurrencies[selectedCurrency]

                    btnMainCurrency.setOnClickListener() {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                        builder.setTitle(alertChoiceCurrTitle)
                        builder.setCancelable(true)

                        builder.setSingleChoiceItems(listCurrencies, selectedCurrency) { dialogInterface, index ->
                            selectedCurrency = index
                        }

                        builder.setPositiveButton(alertDialogPositive) { dialogInterface, i ->

                            currencySettingsDao.get().updateDefaultCurrency(0, selectedCurrency)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                    {
                                        btnMainCurrency.text = textBtnMainCurrency + listCurrencies[selectedCurrency]
                                        Toast.makeText(context, alertChoiceCurrToast + listCurrencies[selectedCurrency], Toast.LENGTH_LONG).show()
                                        dialogInterface.dismiss()
                                    },
                                    {
                                        dialogInterface.dismiss()
                                        it.printStackTrace()
                                    }
                                )
                        }

                        builder.setNegativeButton(alertDialogNegative) { dialogInterface, i ->
                            //sngleChoiceListener.onNegativeBtnClicked()
                        }
                        builder.show()
                    }
                },
                {
                    it.printStackTrace()
                } ))
    }
    //endregion

    //region export to CSV file
    private fun exportToCsvFile(view: View)
    {
        val pathProviderText: String = "com.genesiseternity.incomemate.fileprovider"
        val nameFileCSV: String = "data.csv"

        val historyRecyclerModels: ArrayList<HistoryRecyclerModel> = ArrayList()
        val map: HashMap<List<PieChartCategoriesEntity>, List<PieChartCategoriesTitleEntity>> = HashMap()

        compositeDisposable.add(Observable.zip(
            Observable.fromCallable { pieChartCategoriesDao.get().getSortedPieChartCategoriesData() },
            Observable.fromCallable { pieChartCategoriesTitleDao.get().getAllCategoriesTitleData() }
        ) { list1, list2 -> map[list1] = list2
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val stringBuilder: StringBuilder = StringBuilder()

                    var amountCash: Float = 0.0f
                    var historyRecyclerModel: HistoryRecyclerModel = HistoryRecyclerModel()
                    var pieChartCategoriesEntitiesLength: Int

                    for ((pieChartCategoriesEntities, pieChartCategoriesTitleEntities) in map)
                    {
                        pieChartCategoriesEntitiesLength = pieChartCategoriesEntities.size

                        for (i in 0 until pieChartCategoriesEntitiesLength)
                        {
                            val isUniquePage: Boolean = i == 0 || pieChartCategoriesEntities[i].idPage != pieChartCategoriesEntities[i - 1].idPage

                            if (isUniquePage)
                            {
                                historyRecyclerModel = HistoryRecyclerModel()
                                historyRecyclerModel.date = pieChartCategoriesEntities[i].idPage.toString()

                                amountCash = 0.0f
                            }

                            val tempAmountCurrency: String = pieChartCategoriesEntities[i].amountCategory.replaceToRegex()
                            if (tempAmountCurrency.isNotEmpty())
                            {
                                amountCash += tempAmountCurrency.toFloat()
                            }

                            if ((i < pieChartCategoriesEntitiesLength - 1 && pieChartCategoriesEntities[i].idPage != pieChartCategoriesEntities[i + 1].idPage)
                                || i == pieChartCategoriesEntitiesLength - 1)
                            {
                                historyRecyclerModel.amountCash = amountCash.toString()
                            }

                            if (isUniquePage)
                            {
                                historyRecyclerModels.add(historyRecyclerModel)
                            }

                            for (j in pieChartCategoriesTitleEntities.indices)
                            {
                                if (pieChartCategoriesTitleEntities[j].id == pieChartCategoriesEntities[i].id)
                                {
                                    historyRecyclerModels.add(HistoryRecyclerModel(
                                        "",
                                        pieChartCategoriesEntities[i].amountCategory,
                                        pieChartCategoriesTitleEntities[j].titleCategory,
                                        pieChartCategoriesEntities[i].idPage.toString(),
                                        pieChartCategoriesTitleEntities[j].idCardViewIcon,
                                        0,
                                        pieChartCategoriesTitleEntities[j].idColorIcon,
                                        0
                                    ))

                                }
                            }
                        }
                    }

                    stringBuilder.append(appendStrBuilderCsv)

                    for (i in historyRecyclerModels.indices)
                    {
                        val outDate = convertDate(historyRecyclerModels[i].date)

                        stringBuilder.append("\n" +
                                outDate
                                + "," +
                                historyRecyclerModels[i].titleCategoryName
                                + "," +
                                historyRecyclerModels[i].amountCash
                        )
                    }

                    try
                    {
                        val out: FileOutputStream = view.context.openFileOutput(nameFileCSV, Context.MODE_PRIVATE)
                        out.write((stringBuilder.toString()).toByteArray())
                        out.close()

                        val locationFile: File = File(view.context.filesDir, nameFileCSV)
                        val path: Uri = FileProvider.getUriForFile(view.context, pathProviderText, locationFile)
                        val fileIntent: Intent = Intent(Intent.ACTION_SEND)
                        fileIntent.type = "text/csv"
                        fileIntent.putExtra(Intent.EXTRA_SUBJECT, titleCsvFileName + Calendar.getInstance().time)
                        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        fileIntent.putExtra(Intent.EXTRA_STREAM, path)
                        startActivity(Intent.createChooser(fileIntent, "Send Mail"))
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }

                },
                {

                },
                {
                }
            ))
    }

    private fun convertDate(date: String) : String
    {
        var dateDay: String = ""

        if (date.isNotEmpty())
        {
            val dateFormatDay: DateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val calendar: Calendar = Calendar.getInstance()
            calendar.clear()
            calendar.add(Calendar.DATE, Integer.parseInt(date))
            dateDay = dateFormatDay.format(calendar.time)
        }
        return dateDay
    }
    //endregion

    private fun openPrivacyPolicy()
    {
        val baseUrl: String = "https://sites.google.com/view/privacypolicyincomemate/"
        val uri: Uri = Uri.parse(baseUrl)
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    //region Translate Language
    private lateinit var listLanguages: Array<String>
    private var selectedLanguage : Int = 0

    private fun translateLanguage()
    {
        listLanguages = resources.getStringArray(R.array.list_languages)
        val languageBtn: Button = binding.languageBtn

        compositeDisposable.add(currencySettingsDao.get().getDefaultLanguageByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    selectedLanguage = it

                    languageBtn.text = textLanguageBtn + listLanguages[selectedLanguage]

                    languageBtn.setOnClickListener() {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                        builder.setTitle(alertTranslateTitle)
                        builder.setCancelable(true)

                        builder.setSingleChoiceItems(listLanguages, selectedLanguage) { dialogInterface, index ->
                            selectedLanguage = index
                        }

                        builder.setPositiveButton(alertDialogPositive) { dialogInterface, i ->

                            compositeDisposable.add(currencySettingsDao.get().updateDefaultLanguage(0, selectedLanguage)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                    {
                                        //LanguageConfig(this.resources).setLanguage(selectedLanguage)
                                        //languageConfig.get().resources = this.resources

                                        languageConfig.get().setLanguage(selectedLanguage)
                                        languageConfig.get().resources = this.resources
                                        languageConfig.get().setLanguage(selectedLanguage)

                                        languageBtn.text = textLanguageBtn + listLanguages[selectedLanguage]
                                        Toast.makeText(context, alertTranslateToast + listLanguages[selectedLanguage], Toast.LENGTH_LONG).show()
                                        dialogInterface.dismiss()

                                        val intent: Intent = Intent(context, MainActivity::class.java)
                                        startActivity(intent)
                                        //(activity as MainActivity).recreate()

                                    },
                                    {
                                        it.printStackTrace()
                                    }
                                ))
                        }

                        builder.setNegativeButton(alertDialogNegative) { dialogInterface, i -> }
                        builder.show()
                    }
                },
                {

                }
            ))
    }
    //endregion

    //region Passcode
    private fun openPagePasscode(view: View)
    {
        val listAction : Array<String> = arrayOf("Включить код-пароль", "Сменить код-пароль", "Выключить код-пароль")

        val viewDialog: View = layoutInflater.inflate(R.layout.custom_dialog_setting_passcode, null)
        val listView = viewDialog.findViewById<ListView>(R.id.listViewDialogPasscode)
        val adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1 , listAction)

        compositeDisposable.add(currencySettingsDao.get().getEnabledPasscodeByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                    listView.setOnItemClickListener { parent, view, position, id ->
                        if (it) {
                            if (position == 0) {
                                view.isEnabled = false
                                return@setOnItemClickListener
                            }
                        } else {
                            if (position == 2) {
                                view.isEnabled = false
                                return@setOnItemClickListener
                            }
                        }

                        // FIXME: destroy display a dialog box after exiting an activity -> alertDialog.show()
                        val intent: Intent = Intent(context, Passcode::class.java)
                        intent.putExtra("stateActionId", position)
                        startActivity(intent)
                    }

                    listView.adapter = adapter

                    builder.setTitle("Код-пароль")
                    .setCancelable(true)
                    //.setItems(listAction) { dialogInterface, i ->
                    //    val intent: Intent = Intent(context, Passcode::class.java)
                    //    intent.putExtra("stateActionId", i)
                    //    startActivity(intent)
                    //    dialogInterface.dismiss()
                    //}
                    .setNegativeButton("Отмена") { dialogInterface, i ->
                        dialogInterface.cancel()
                    }
                    .setView(viewDialog)

                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                },
                {
                    it.printStackTrace()
                }
            ))
    }

    private fun sendStateActionAlert(listAction: Array<String>)
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setTitle("Код-пароль")
            .setCancelable(true)
            .setItems(listAction) { dialogInterface, i ->
                val intent: Intent = Intent(context, Passcode::class.java)
                intent.putExtra("stateActionId", i)
                startActivity(intent)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Отмена") { dialogInterface, i ->
                dialogInterface.cancel()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
    //endregion
}