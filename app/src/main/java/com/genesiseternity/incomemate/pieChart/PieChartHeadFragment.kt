package com.genesiseternity.incomemate.pieChart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.genesiseternity.incomemate.databinding.FragmentPieChartHeadBinding
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.PieChartCategoriesDao
import com.genesiseternity.incomemate.room.PieChartDao
import com.genesiseternity.incomemate.room.entities.PieChartEntity
import com.jakewharton.rxbinding4.view.clicks
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class PieChartHeadFragment : DaggerFragment()
{
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var pieChartDao: dagger.Lazy<PieChartDao>
    @Inject lateinit var currencySettingsDao: dagger.Lazy<CurrencySettingsDao>

    private lateinit var binding: FragmentPieChartHeadBinding
    private lateinit var pieChartHeadViewModel: PieChartHeadViewModel

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerCallback: ViewPager2.OnPageChangeCallback
    private lateinit var pagerAdapter: FragmentStateAdapter
    private val NUM_PAGES: Int = 376200 // 1970 - 3000

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var monetaryAccountSpinner: Spinner
    private lateinit var spinnerAdapter: SpinnerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPieChartHeadBinding.inflate(inflater, container, false)
        val view: View = binding.root
        monetaryAccountSpinner = binding.spinnerMonetaryAccount

        pieChartHeadViewModel = ViewModelProvider(this, providerFactory)[PieChartHeadViewModel::class.java]

        pieChartHeadViewModel.getListCurrencyRecyclerModel().observe(viewLifecycleOwner) { model ->
            spinnerAdapter = SpinnerAdapter(view.context, R.layout.row_spinner_item, model)
            monetaryAccountSpinner.adapter = spinnerAdapter

            updateSelectedDefaultMonetaryAccount()
        }

        val centerDateBtn: Button = binding.centerDateBtn
        viewPager = binding.pagerPieChart
        pagerAdapter = ScreenSlideViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter

        val calFirst: Calendar = Calendar.getInstance()
        val calSecond: Calendar = Calendar.getInstance()
        calFirst.clear()
        val daysBetween: Int = pieChartHeadViewModel.daysBetween(calFirst.time, calSecond.time)

        viewPager.setCurrentItem(daysBetween, false)
        viewPager.offscreenPageLimit = 1
        val saveCurrentItem: Int = viewPager.currentItem

        centerDateBtn.text = pieChartHeadViewModel.setCurrentDate()

        if (pieChartHeadViewModel.getCounterUpdatePage > 0)
        {
            val daysBetweens: Int = pieChartHeadViewModel.daysBetween(calFirst.time, pieChartHeadViewModel.getCurrentDate)
            viewPager.setCurrentItem(daysBetweens, false)
        }

        compositeDisposable.add(binding.leftDateBtn.clicks()
            .subscribe {
                if (viewPager.currentItem in 1 until NUM_PAGES) {
                    viewPager.currentItem = viewPager.currentItem - 1
                    centerDateBtn.text = pieChartHeadViewModel.initDateFormat(viewPager.currentItem, saveCurrentItem)
                }
            })

        compositeDisposable.add(binding.rightDateBtn.clicks()
            .subscribe {
                if (viewPager.currentItem >= 0 && viewPager.currentItem < NUM_PAGES-1) {
                    viewPager.currentItem = viewPager.currentItem + 1
                    centerDateBtn.text = pieChartHeadViewModel.initDateFormat(viewPager.currentItem, saveCurrentItem)
                }
            })

        viewPagerCallback = object : ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                centerDateBtn.text = pieChartHeadViewModel.initDateFormat(position, saveCurrentItem)
                Log.d("PieChartFragment", " - " + position)
            }
        }
        viewPager.registerOnPageChangeCallback(viewPagerCallback)

        return view
    }

    override fun onDestroy()
    {
        super.onDestroy()
        viewPager.unregisterOnPageChangeCallback(viewPagerCallback)
        compositeDisposable.dispose()
    }

    inner class ScreenSlideViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle)
    {
        override fun createFragment(position: Int): Fragment {
            val fragmentView: PieChartFragmentView = PieChartFragmentView()
            val args: Bundle = Bundle()
            args.putInt("id_page", position)
            fragmentView.arguments = args

            val pieChartEntities: PieChartEntity = PieChartEntity(
                position,
                0
            )

            compositeDisposable.add(pieChartDao.get().insertPieChartData(pieChartEntities)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                    },
                    {
                        it.printStackTrace()
                    }
                ))

            return fragmentView
        }

        override fun getItemViewType(position: Int): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getItemCount(): Int {
            return NUM_PAGES
        }
    }

    private fun setSelectedMonetaryAccountDefault()
    {
        compositeDisposable.add(currencySettingsDao.get().getDefaultIdCurrencyAccountByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { monetaryAccountSpinner.setSelection(it) }, { it.printStackTrace() } ))
    }

    private fun updateSelectedDefaultMonetaryAccount()
    {
        setSelectedMonetaryAccountDefault()

        monetaryAccountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                compositeDisposable.add(currencySettingsDao.get().updateDefaultIdCurrencyAccount(position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( { }, { it.printStackTrace() } ))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}