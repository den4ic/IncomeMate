package com.genesiseternity.incomemate.pieChart

import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.FragmentPieChartBinding
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.PieChartDao
import com.genesiseternity.incomemate.room.entities.PieChartEntity
import com.genesiseternity.incomemate.wallet.CurrencyRecyclerModel
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PieChartFragment : DaggerFragment() {

    private lateinit var binding: FragmentPieChartBinding

    private lateinit var pieChartViewModel: PieChartViewModel

    @Inject lateinit var pieChartDao: dagger.Lazy<PieChartDao>
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var currencyDetailsDao: dagger.Lazy<CurrencyDetailsDao>

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    //@Override
    //public void onCreate(Bundle savedInstanceState)
    //{
    //    super.onCreate(savedInstanceState)
    //    pieChartViewModel = new ViewModelProvider(this, providerFactory).get(PieChartViewModel.class)
    //}


    override fun onDestroy() {
        super.onDestroy()
        viewPager.unregisterOnPageChangeCallback(viewPagerCallback)
        compositeDisposable.dispose()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //AndroidSupportInjection.inject(this)
        binding = FragmentPieChartBinding.inflate(inflater, container, false)
        val view: View = binding.root

        pieChartViewModel = ViewModelProvider(this, providerFactory).get(PieChartViewModel::class.java)

        initAllMonetaryAccount(view)

        return view
    }



    private lateinit var viewPager: ViewPager2
    lateinit var viewPagerCallback: ViewPager2.OnPageChangeCallback
    private lateinit var pagerAdapter: FragmentStateAdapter
    private val NUM_PAGES: Int = 376200 // 1970 - 3000

    private lateinit var currentDate: Date
    private lateinit var dateFormat: DateFormat
    //private val referenceDate: String = "01.01.1970"
    private val computePatternDate: String = "dd.MM.yyyy"
    private val uiPatternDate: String = "EEE, dd MMMM yyyy"
    private lateinit var centerDateBtn: Button
    private lateinit var leftDateBtn: Button
    private lateinit var rightDateBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        centerDateBtn = binding.centerDateBtn
        leftDateBtn = binding.leftDateBtn
        rightDateBtn = binding.rightDateBtn

        viewPager = binding.pagerPieChart
        pagerAdapter = ScreenSlideViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter

        currentDate = Date()


        /////////val calendar: Calendar = Calendar.getInstance()
        /////////calendar.clear()
        /////////calendar.add(Calendar.DATE, Integer.parseInt(historyRecyclerModel.date))


        dateFormat = SimpleDateFormat(uiPatternDate, Locale.getDefault())
        centerDateBtn.text = dateFormat.format(currentDate)
        dateFormat = SimpleDateFormat(computePatternDate, Locale.getDefault())

        val calFirst: Calendar = Calendar.getInstance()
        val calSecond: Calendar = Calendar.getInstance()

        //calFirst.time = dateFormat.parse(referenceDate) as Date
        //calSecond.time = dateFormat.parse(dateFormat.format(currentDate)) as Date
        calFirst.clear()

        val daysBetween: Int = daysBetween(calFirst.time, calSecond.time)

        viewPager.setCurrentItem(daysBetween, false)
        viewPager.offscreenPageLimit = 1
        val saveCurrentItem: Int = viewPager.currentItem

        leftDateBtn.setOnClickListener()
        {
            if (viewPager.currentItem in 1 until NUM_PAGES) {
                viewPager.currentItem = viewPager.currentItem - 1
                initDateFormat(viewPager.currentItem, saveCurrentItem)
            }
        }

        rightDateBtn.setOnClickListener()
        {

            if (viewPager.currentItem >= 0 && viewPager.currentItem < NUM_PAGES-1)
            {
                viewPager.currentItem = viewPager.currentItem + 1
                initDateFormat(viewPager.currentItem, saveCurrentItem)
            }
        }

        viewPagerCallback = object : ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                initDateFormat(position, saveCurrentItem)
                Log.d("PieChartFragment", " - " + position)
            }
        }
        viewPager.registerOnPageChangeCallback(viewPagerCallback)

    }

    inner class ScreenSlideViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle)
    {
        override fun createFragment(position: Int): Fragment {
            val fragmentView: PieChartFragmentView = PieChartFragmentView()
            val args: Bundle = Bundle()
            args.putInt("id_page", position)
            fragmentView.arguments = args

            //PieChartEntity pieChartEntities = new PieChartEntity()
            //pieChartEntities.setIdPage(position)
            //PieChartCategoriesEntity pieChartCategoriesEntity = new PieChartCategoriesEntity()
            //pieChartCategoriesEntity.setId(0)
            //pieChartCategoriesEntity.setTitleCategory("test1")
            //pieChartCategoriesEntity.setAmountCategory("test1")
            //pieChartCategoriesEntity.setCurrencyType(0)
            //pieChartCategoriesEntity.setIdIcon(0)
            //pieChartCategoriesEntity.setIdColorIcon(0)
            //pieChartEntities.setPieChartCategoriesEntity(pieChartCategoriesEntity)

            val pieChartEntities: PieChartEntity = PieChartEntity(
                position,
                0
            )

            pieChartDao.get().insertPieChartData(pieChartEntities)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("123", "1 - 1")
                    },
                    {
                        Log.d("123", "2 - 2")
                    }
                )


            /*
            Log.d("PieChartFragment", "id page = " + args.getInt("id_page"))

            pieChartDao.get().getAllSortedPieChartData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<PieChartEntity>>()
                    {
                        @Override
                        public void accept(List<PieChartEntity> pieChartEntitiesList) throws Throwable
                        {
                            PieChartEntity[] pieChartEntities = new PieChartEntity[pieChartEntitiesList.size()]

                            for (int i = 0 i < pieChartEntities.length i++)
                            {
                                PieChartEntity pieChartEntity = new PieChartEntity()
                                pieChartEntity.setIdPage(position)
                                pieChartEntity.setId(i)
                                pieChartEntities[i] = pieChartEntity
                            }

                            pieChartDao.get().insertPieChartData(pieChartEntities)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new CompletableObserver()
                                    {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d)
                                        {
                                            Log.d("PieChartFragment", "1 - 1")
                                        }

                                        @Override
                                        public void onComplete()
                                        {
                                            Log.d("PieChartFragment", "2 - 2")
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e)
                                        {
                                            Log.d("PieChartFragment", "3 - 3")
                                        }
                                    })
                        }
                    })
             */


            return fragmentView


        }

        //@NonNull
        //@Override
        //public Fragment createFragment(int position)
        //{
        //
        //}

        override fun getItemViewType(position: Int): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getItemCount(): Int {
            return NUM_PAGES
        }





    }

    //public int SecondsBetween(Date d1, Date d2){
    //    //return (int)((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)) // day
    //    return (int)((d2.getTime() - d1.getTime()) / (1000)) // sec
    //}

    private fun daysBetween(d1: Date, d2: Date): Int
    {
        return ((d2.time - d1.time) / (1000 * 60 * 60 * 24)).toInt() // day
    }

    private fun initDateFormat(currentDay: Int, saveCurrentDay: Int)
    {
        val counterDay: Int = currentDay - saveCurrentDay
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DATE, counterDay)
        dateFormat = SimpleDateFormat(uiPatternDate, Locale.getDefault()) // EEE, dd MMMM yyyy
        val dt: String = dateFormat.format(calendar.time)
        centerDateBtn.text = dt

    }




    private var currencyRecyclerModel: ArrayList<CurrencyRecyclerModel> = ArrayList()

    private fun initAllMonetaryAccount(view: View)
    {
        val imageCurrencyType: TypedArray = resources.obtainTypedArray(R.array.image_currency_type)

        val allMonetaryAccount: Spinner = binding.spinnerMonetaryAccount
        val listMonetaryAccounts: String = resources.getString(R.string.list_monetary_account)

        currencyRecyclerModel.add(CurrencyRecyclerModel(0, listMonetaryAccounts, "0",0, imageCurrencyType.getResourceId(0, 0), 0))

        val disposableSortedData: Disposable = currencyDetailsDao.get().getAllSortedCurrencyData()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { t -> Observable.fromIterable(t) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    currencyDetailsEntities ->

                    currencyRecyclerModel.add(CurrencyRecyclerModel(
                        currencyDetailsEntities.id,
                        currencyDetailsEntities.titleCurrency,
                        currencyDetailsEntities.amountCurrency,
                        currencyDetailsEntities.currencyType,
                        imageCurrencyType.getResourceId(currencyDetailsEntities.idCardViewIcon, 0),
                        currencyDetailsEntities.idColorIcon)
                    )

                },
                {

                },
                {
                    val customAdapter: SpinnerAdapter = SpinnerAdapter(view.context, R.layout.row_spinner_item, currencyRecyclerModel)
                    allMonetaryAccount.adapter = customAdapter

                    updateAmountCurrency()
                }
            )

        compositeDisposable.add(disposableSortedData)

        /*
        DBHelper dbHelper = new DBHelper(getActivity())
        Cursor res = dbHelper.getSortedCurrencyData()
        int countRecyclerCurrency = res.getCount()

        currencyRecyclerModel.add(new CurrencyRecyclerModel(0, listMonetaryAccounts[0], "0",0, imageCurrencyType.getResourceId(0, 0), 0))

        if (countRecyclerCurrency == 0)
        {

        }
        else
        {
            while (res.moveToNext())
            {
                Log.d("123", "1 - " + res.getInt(0))
                Log.d("123", "2 - " + res.getString(1))
                Log.d("123", "3 - " + res.getString(2))
                Log.d("123", "4 - " + res.getInt(3))
                Log.d("123", "5 - " + res.getInt(4))
                Log.d("123", "6 - " + res.getInt(5))

                currencyRecyclerModel.add(new CurrencyRecyclerModel(res.getInt(0), res.getString(1), res.getString(2), res.getInt(3), imageCurrencyType.getResourceId(res.getInt(4), 0), res.getInt(5)))
            }
        }

        //for (int i = 0 i < currencyRecycler.size() i++)
        //    Log.d("123", "1 - " + currencyRecycler.get(i).getTitleCurrencyName())

        SpinnerAdapter customAdapter = new SpinnerAdapter(view.getContext(), R.layout.row_spinner_item, currencyRecyclerModel)
        allMonetaryAccount.setAdapter(customAdapter)

        UpdateAmountCurrency()
         */
    }

    @Inject lateinit var currencyFormat: dagger.Lazy<CurrencyFormat>
    //CurrencyFormat currencyFormat = new CurrencyFormat()

    private fun updateAmountCurrency()
    {
        //float[] data = new float[currencyRecyclerModel.size()]
        var amountCurrency: Float = 0f

        for (i in currencyRecyclerModel.indices)
        {
            val tempAmountCurrency: String = currencyRecyclerModel.get(i).amountCurrency.replace("[^\\d.-]".toRegex(), "")

            if (tempAmountCurrency.isNotEmpty())
            {
                amountCurrency += tempAmountCurrency.toFloat()
            }
        }
        currencyRecyclerModel.get(0).amountCurrency = if (amountCurrency != 0f) currencyFormat.get().setStringTextFormatted(amountCurrency.toString()) else "0"
    }
}