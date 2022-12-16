package com.genesiseternity.incomemate.pieChart

import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.databinding.FragmentPieChartViewBinding
import com.genesiseternity.incomemate.room.PieChartCategoriesDao
import com.genesiseternity.incomemate.room.PieChartCategoriesTitleDao
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PieChartFragmentView : DaggerFragment(), IPieChartCategoryView {

    private lateinit var binding: FragmentPieChartViewBinding
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var currencyFormat: dagger.Lazy<CurrencyFormat>

    @Inject lateinit var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>

    @Inject lateinit var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>

    private lateinit var viewTemp: View
    private lateinit var listCategory: Array<String>
    private lateinit var imageCategoryType: TypedArray
    private var colorRed: Int = 0
    private var colorGreen: Int = 0

    private var idPage: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPieChartViewBinding.inflate(inflater, container, false)
        viewTemp = binding.root

        listCategory = resources.getStringArray(R.array.list_category_pie_chart)
        imageCategoryType = resources.obtainTypedArray(R.array.image_category_type)
        colorRed = ContextCompat.getColor(viewTemp.context, R.color.red)
        colorGreen = ContextCompat.getColor(viewTemp.context, R.color.green)
        defaultCurrencyType = (activity as MainActivity).getDefaultCurrencyType()


        val args: Bundle? = arguments
        idPage = args?.getInt("id_page")!!

        Log.d("PieChartFragmentView", " - " + idPage)



        val pieChartCategoriesTitleEntities: ArrayList<PieChartCategoriesTitleEntity> = ArrayList(listCategory.size)

        val rnd: Random = Random()
        val colorRandList: IntArray = IntArray(size = listCategory.size) { Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)) }

        for (i in listCategory.indices)
        {
            val pieChartCategoriesTitleEntity: PieChartCategoriesTitleEntity = PieChartCategoriesTitleEntity(
                i,
                listCategory[i],
                defaultCurrencyType,
                //imageCategoryType.getResourceId(i, 0),
                i,
                colorRandList[i])

            pieChartCategoriesTitleEntities.add(pieChartCategoriesTitleEntity)
        }



        pieChartCategoriesTitleDao.get().insertPieChartCategoriesTitleData(*pieChartCategoriesTitleEntities.toTypedArray())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    initPieChart()
                },
                {
                    initPieChart()
                }
            )


        //InitPieChart()
        //FillPieChart()

        //testFillPieChart()

        return viewTemp
    }


    private fun testFillPieChart()
    {
        pieChartCategoryArrayList = ArrayList()
        adapter = PieChartCategoryViewAdapter(viewTemp.context, pieChartCategoryArrayList, this)
        binding.gridViewCategory.adapter = adapter

        val colorRandList: IntArray = IntArray(listCategory.size)

        for (i in listCategory.indices)
        {
            val rnd: Random = Random()
            colorRandList[i] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

            pieChartCategoryArrayList.add(PieChartCategory(
                i,
                listCategory[i],
                rnd.nextInt(4096).toString(),
                imageCategoryType.getResourceId(i, 0),
                colorRandList[i],
                defaultCurrencyType))

        }
        pieChartCategoryArrayList.add(PieChartCategory(-1, "Добавить", "", R.drawable.ic_baseline_add_circle_24, 0, defaultCurrencyType))
        fillPieChart()
    }


    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private var defaultCurrencyType: Int = 0

    private lateinit var pieChartCategoryArrayList: ArrayList<PieChartCategory>
    private lateinit var adapter: PieChartCategoryViewAdapter

    private fun initPieChart()
    {
        pieChartCategoryArrayList = ArrayList()
        adapter = PieChartCategoryViewAdapter(viewTemp.context, pieChartCategoryArrayList, this)
        binding.gridViewCategory.adapter = adapter

        //pieChartCategoriesDao.get().getAllSortedPieChartCategoriesData()
        val disposablePieChartCategories: Disposable = pieChartCategoriesDao.get().getPieChartCategoryByIdPage(idPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    pieChartCategoriesEntities ->

                    val disposablePieChartCategoriesTitle: Disposable = pieChartCategoriesTitleDao.get().getAllPieChartCategoriesTitleData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                pieChartCategoriesTitleEntities ->

                                if (pieChartCategoriesEntities.size > 0)
                                {
                                    for (i in pieChartCategoriesTitleEntities.indices)
                                    {
                                        pieChartCategoryArrayList.add(PieChartCategory(
                                            pieChartCategoriesTitleEntities[i].id,
                                            pieChartCategoriesTitleEntities[i].titleCategory,
                                            "0",
                                            imageCategoryType.getResourceId(
                                                pieChartCategoriesTitleEntities[i].idCardViewIcon, 0),
                                                pieChartCategoriesTitleEntities[i].idColorIcon,
                                                pieChartCategoriesTitleEntities[i].currencyType))

                                        for (j in pieChartCategoriesEntities.indices)
                                        {
                                            if (i == pieChartCategoriesEntities[j].id)
                                            {
                                                pieChartCategoryArrayList.removeAt(pieChartCategoriesEntities[j].id)

                                                pieChartCategoryArrayList.add(PieChartCategory(
                                                    pieChartCategoriesEntities[j].id,
                                                    pieChartCategoriesTitleEntities[i].titleCategory,
                                                    pieChartCategoriesEntities[j].amountCategory,
                                                    imageCategoryType.getResourceId(pieChartCategoriesTitleEntities[i].idCardViewIcon, 0),
                                                    pieChartCategoriesTitleEntities[i].idColorIcon,
                                                    pieChartCategoriesTitleEntities[i].currencyType))
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    for (i in pieChartCategoriesTitleEntities.indices)
                                    {
                                        pieChartCategoryArrayList.add(PieChartCategory(
                                            pieChartCategoriesTitleEntities[i].id,
                                            pieChartCategoriesTitleEntities[i].titleCategory,
                                            "0",
                                            imageCategoryType.getResourceId(pieChartCategoriesTitleEntities[i].idCardViewIcon, 0),
                                            pieChartCategoriesTitleEntities[i].idColorIcon,
                                            pieChartCategoriesTitleEntities[i].currencyType))
                                    }
                                }
                                pieChartCategoryArrayList.add(PieChartCategory(-1, "Добавить", "", R.drawable.ic_baseline_add_circle_24, 0, defaultCurrencyType))

                                fillPieChart()
                                adapter.notifyDataSetChanged()
                            },
                            {

                            }
                        )

                    compositeDisposable.add(disposablePieChartCategoriesTitle)
                },
                {

                }
            )

        compositeDisposable.add(disposablePieChartCategories)

        //for (int i = 0 i < listCategory.length i++)
        //{
        //    Random rnd = new Random()
        //    colorRandList[i] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        //
        //    pieChartCategoryArrayList.add(new PieChartCategory(
        //            i,
        //            listCategory[i],
        //            String.valueOf(rnd.nextInt(4096)),
        //            imageCategoryType.getResourceId(i, 0),
        //            colorRandList[i],
        //            defaultCurrencyType))
        //}

        //pieChartCategoryArrayList.add(new PieChartCategory(-1, "Добавить", "", R.drawable.ic_baseline_add_circle_24, 0, defaultCurrencyType))
        //FillPieChart()
        //adapter.notifyDataSetChanged()

        /*
        for (int i = 0 i < listCategory.length i++)
        {
            Random rnd = new Random()
            colorRandList[i] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

            pieChartCategoryArrayList.add(new PieChartCategory(
                    i,
                    listCategory[i],
                    String.valueOf(rnd.nextInt(4096)),
                    imageCategoryType.getResourceId(i, 0),
                    colorRandList[i],
                    defaultCurrencyType))

        }
        pieChartCategoryArrayList.add(new PieChartCategory(-1, "Добавить", "", R.drawable.ic_baseline_add_circle_24, 0, defaultCurrencyType))
         */

    }

    override fun onItemClick(pos: Int)
    {
        val insertIndex: Int = pieChartCategoryArrayList.size-1

        if (pos != insertIndex)
        {
            goToSelectedActivity(pos)
        }
        else
        {
            var numAccount: Int = insertIndex
            val titleCategoryName: String = "Новая категория №" + ++numAccount
            pieChartCategoryArrayList.add(insertIndex, PieChartCategory(pos, titleCategoryName, "0", imageCategoryType.getResourceId(0, 0), 0, defaultCurrencyType))
            // adapter.notifyItemInserted(insertIndex)
            adapter.notifyDataSetChanged()

            val pieChartCategoriesTitleEntity: PieChartCategoriesTitleEntity = PieChartCategoriesTitleEntity(
                insertIndex,
                titleCategoryName,
                defaultCurrencyType,
                0,
                colorGreen)

            pieChartCategoriesTitleDao.get().insertPieChartCategoriesTitleData(pieChartCategoriesTitleEntity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        goToSelectedActivity(insertIndex)
                    },
                    {
                        goToSelectedActivity(insertIndex)
                    }
                )
        }
    }

    private fun goToSelectedActivity(idPos: Int)
    {
        val intent: Intent = Intent(viewTemp.context, CategoryActivity::class.java)
        intent.putExtra("idCategory", pieChartCategoryArrayList[idPos].idCategory)
        intent.putExtra("idPage", idPage)
        intent.putExtra("titleCategoryName", pieChartCategoryArrayList[idPos].titleCategoryName)
        intent.putExtra("amountCategory", pieChartCategoryArrayList[idPos].amountCategory)
        intent.putExtra("imageCategory", pieChartCategoryArrayList[idPos].imageCategory)
        intent.putExtra("selectedColorId", pieChartCategoryArrayList[idPos].selectedColorId)
        intent.putExtra("defaultCurrencyType", pieChartCategoryArrayList[idPos].currencyType)
        startActivity(intent)
    }

    private fun fillPieChart()
    {
        val sizeCategoryArrayList: Int = pieChartCategoryArrayList.size-1
        val data: FloatArray = FloatArray(sizeCategoryArrayList)
        val color: IntArray = IntArray(size = sizeCategoryArrayList)

        var amountCurrency: Float = 0f

        for (i in 0 until sizeCategoryArrayList)
        {
            //String tempAmountCurrency = recyclerCurrencies.get(i).getAmountCurrency().replaceAll("[^0-9]", "")
            val tempAmountCurrency: String = pieChartCategoryArrayList[i].amountCategory.replace("[^\\d.-]".toRegex(), "")
            val tempSelectedColorId: Int = pieChartCategoryArrayList[i].selectedColorId

            if (tempAmountCurrency.isNotEmpty())
            {
                data[i] = (if (tempAmountCurrency.startsWith("-")) "0" else tempAmountCurrency).toFloat()
                amountCurrency += tempAmountCurrency.toFloat()
            }

            color[i] = tempSelectedColorId
            //color = intArrayOf(tempSelectedColorId)
        }

        val textExpenses: TextView = binding.textExpenses
        textExpenses.text = if (amountCurrency != 0f) currencyFormat.get().setStringTextFormatted(amountCurrency.toString()) else "0"
        textExpenses.setTextColor(colorRed)
        binding.textIncome.setTextColor(colorGreen)


        binding.pieChartView.setDataPieChart(data, color)
    }

}