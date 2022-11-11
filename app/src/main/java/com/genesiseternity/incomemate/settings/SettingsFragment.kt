package com.genesiseternity.incomemate.settings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.databinding.FragmentSettingsBinding
import com.genesiseternity.incomemate.room.*
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SettingsFragment : DaggerFragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject lateinit var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>
    @Inject lateinit var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>
    @Inject lateinit var pieChartDao: dagger.Lazy<PieChartDao>
    @Inject lateinit var currencyColorDao: dagger.Lazy<CurrencyColorDao>
    @Inject lateinit var currencyDetailsDao: dagger.Lazy<CurrencyDetailsDao>
    @Inject lateinit var currencySettingsDao: dagger.Lazy<CurrencySettingsDao>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view: View = binding.root

        singleChoiceCurrencyItems()
        removeAllDataSettings()

        return view
    }

    private fun removeAllDataSettings()
    {
        binding.removeAllData.setOnClickListener()
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Удалить данные")
            builder.setCancelable(true)
            builder.setMessage("Все ваши данные (счета, категории, история операций) будут безвозвратно удалены.")

            builder.setPositiveButton("Удалить все данные") { dialogInterface, i ->
                currencySettingsDao.get().deleteAllCurrencySettingsData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d("123", "SettingsFragment - deleteAllCurrencySettingsData")
                        },
                        {
                        }
                    )
                currencyDetailsDao.get().deleteAllCurrencyData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d("123", "SettingsFragment - deleteAllCurrencyData")
                        },
                        {
                        }
                    )
                currencyColorDao.get().deleteAllCurrencyColorData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d("123", "SettingsFragment - deleteAllCurrencyColorData")
                        },
                        {
                        }
                    )
                pieChartDao.get().deleteAllPieChartData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d("123", "SettingsFragment - deleteAllPieChartData")
                        },
                        {
                        }
                    )
                pieChartCategoriesDao.get().deleteAllPieChartCategoriesData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d("123", "SettingsFragment - deleteAllPieChartCategoriesData")
                        },
                        {
                        }
                    )
                pieChartCategoriesTitleDao.get().deleteAllPieChartCategoriesTitleData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.d("123", "SettingsFragment - deleteAllPieChartCategoriesTitleData")
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
    }


    //region Currency List
    private lateinit var listCurrencies: Array<String>
    private var selectedCurrency: Int = 0

    private fun singleChoiceCurrencyItems()
    {
        selectedCurrency = (activity as MainActivity).getDefaultCurrencyType()

        listCurrencies = resources.getStringArray(R.array.list_currencies)
        val btnMainCurrency: Button = binding.BtnMainCurrency
        btnMainCurrency.text = "Основная валюта:\n" + listCurrencies[selectedCurrency]

        btnMainCurrency.setOnClickListener() {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Основная валюта")
            builder.setCancelable(true)

            builder.setSingleChoiceItems(listCurrencies, selectedCurrency,) { dialogInterface, index ->
                selectedCurrency = index
                //btnMainCurrency.setText("Основаная валюта: " + listCurrencies[selectedCurrency])
                //Toast.makeText(getActivity(), "Выбрана валюта: " + listCurrencies[selectedCurrency], Toast.LENGTH_LONG).show()
                //dialogInterface.dismiss()
            }

            builder.setPositiveButton("Готово") { dialogInterface, i ->
                //sngleChoiceListener.onPositiveBtnClicked(listCurrencies, pos)

                btnMainCurrency.text = "Основная валюта:\n" + listCurrencies[selectedCurrency]
                Toast.makeText(activity, "Выбрана валюта: " + listCurrencies[selectedCurrency], Toast.LENGTH_LONG).show()

                (activity as MainActivity).setDefaultCurrencyType(selectedCurrency)
                dialogInterface.dismiss()
            }

            builder.setNegativeButton("Отменить") { dialogInterface, i ->
                //sngleChoiceListener.onNegativeBtnClicked()
            }
            builder.show()
            //val alertDialog: AlertDialog = builder.create()
            //alertDialog.setCanceledOnTouchOutside(true)
            //alertDialog.show()
        }
    }
    //endregion

}