package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.FragmentPieChartViewBinding
import com.genesiseternity.incomemate.utils.getEnum
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class PieChartFragmentView : DaggerFragment()
{
    @Inject lateinit var providerFactory: ViewModelProviderFactory

    private val pieChartFragmentViewModel: PieChartFragmentViewModel by lazy {
        ViewModelProvider(this, providerFactory)[PieChartFragmentViewModel::class.java]
    }

    private lateinit var adapter: PieChartCategoryViewAdapter
    private lateinit var selectedCategoryType: StateCategoryType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val binding = FragmentPieChartViewBinding.inflate(inflater, container, false)
        val view = binding.root

        val colorRed = ContextCompat.getColor(view.context, R.color.red)
        val colorGreen = ContextCompat.getColor(view.context, R.color.green)

        val args: Bundle? = arguments
        val idPage = args?.getInt("id_page")!!
        selectedCategoryType = args.getEnum<StateCategoryType>("selectedCategoryType")!!

        binding.textTtitlePieChart.text = if (selectedCategoryType == StateCategoryType.EXPENSES) "Расходы" else "Доходы"

        pieChartFragmentViewModel.idPage = idPage

        pieChartFragmentViewModel.getPieChartCategoryModelListLiveData().observe(viewLifecycleOwner) {
            adapter = PieChartCategoryViewAdapter(view.context, it, pieChartFragmentViewModel)
            binding.gridViewCategory.adapter = adapter
        }

        pieChartFragmentViewModel.getFillPieChartLiveData().observe(viewLifecycleOwner) {

            if (it.textExpenses != null && it.data != null && it.color != null) {

                if (selectedCategoryType == StateCategoryType.EXPENSES)
                {
                    val textExpenses: TextView = binding.textExpenses
                    textExpenses.text = it.textExpenses
                    textExpenses.setTextColor(colorRed)
                    binding.textIncome.setTextColor(colorGreen)
                }
                else
                {
                    val textExpenses: TextView = binding.textExpenses
                    textExpenses.text = it.textExpenses
                    textExpenses.setTextColor(colorGreen)
                    binding.textIncome.setTextColor(colorRed)
                }

                binding.pieChartView.setDataPieChart(it.data, it.color)
            }
        }

        pieChartFragmentViewModel.notifyItemAdapterLiveData.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        return view
    }
}