package com.genesiseternity.incomemate.pieChart

import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.FragmentPieChartViewBinding
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.PieChartCategoriesDao
import com.genesiseternity.incomemate.room.PieChartCategoriesTitleDao
import dagger.android.support.DaggerFragment
import java.util.*
import javax.inject.Inject

class PieChartFragmentView : DaggerFragment() {

    @Inject lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var binding: FragmentPieChartViewBinding
    private lateinit var pieChartFragmentViewModel: PieChartFragmentViewModel
    private lateinit var adapter: PieChartCategoryViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPieChartViewBinding.inflate(inflater, container, false)
        val view = binding.root

        val colorRed = ContextCompat.getColor(view.context, R.color.red)
        val colorGreen = ContextCompat.getColor(view.context, R.color.green)

        pieChartFragmentViewModel = ViewModelProvider(this, providerFactory)[PieChartFragmentViewModel::class.java]

        val args: Bundle? = arguments
        val idPage = args?.getInt("id_page")!!

        Log.d("PieChartFragmentView", "idPage - " + idPage)

        pieChartFragmentViewModel.idPage = idPage

        pieChartFragmentViewModel.getPieChartCategoryModelListLiveData().observe(viewLifecycleOwner) {
            //adapter = PieChartCategoryViewAdapter(view.context, pieChartCategoryModelArrayList, this)
            adapter = PieChartCategoryViewAdapter(view.context, it, pieChartFragmentViewModel)
            binding.gridViewCategory.adapter = adapter
        }

        pieChartFragmentViewModel.getFillPieChartLiveData().observe(viewLifecycleOwner) {

            if (it.textExpenses != null && it.data != null && it.color != null) {
                val textExpenses: TextView = binding.textExpenses
                textExpenses.text = it.textExpenses
                textExpenses.setTextColor(colorRed)
                binding.textIncome.setTextColor(colorGreen)

                binding.pieChartView.setDataPieChart(it.data, it.color)
                //adapter.notifyDataSetChanged()
            }
        }


        return view
    }
}