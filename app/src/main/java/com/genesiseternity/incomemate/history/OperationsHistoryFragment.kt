package com.genesiseternity.incomemate.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.FragmentOperationsHistoryBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class OperationsHistoryFragment : DaggerFragment() {

    @Inject lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var binding: FragmentOperationsHistoryBinding

    private lateinit var historyViewModel: OperationsHistoryViewModel
    private lateinit var adapter: HistoryRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOperationsHistoryBinding.inflate(inflater, container, false)
        val view: View = binding.root

        recyclerView = binding.recyclerHistoryList
        historyViewModel = ViewModelProvider(this, providerFactory)[OperationsHistoryViewModel::class.java]

        adapter = HistoryRecyclerViewAdapter(historyViewModel)
        recyclerView.adapter = adapter

        historyViewModel.initHistoryList()

        historyViewModel.getCurrencyRecyclerModelLiveData().observe(viewLifecycleOwner) { historyRecyclerModels ->
            adapter.setHistoryRecyclerModel(historyRecyclerModels)
        }

        val itemDecorator = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(view.context, R.drawable.row_item_divider)!!)
        recyclerView.addItemDecoration(itemDecorator)

        return view
    }
}