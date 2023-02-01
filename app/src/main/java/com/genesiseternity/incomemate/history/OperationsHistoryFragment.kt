package com.genesiseternity.incomemate.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.FragmentOperationsHistoryBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class OperationsHistoryFragment : DaggerFragment()
{
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    private val historyViewModel by lazy {
        ViewModelProvider(this, providerFactory)[OperationsHistoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOperationsHistoryBinding.inflate(inflater, container, false)
        val view: View = binding.root

        val recyclerView = binding.recyclerHistoryList
        val adapter = HistoryRecyclerViewAdapter(historyViewModel)
        recyclerView.adapter = adapter

        historyViewModel.initHistoryList()

        historyViewModel.historyRecyclerModelLiveData.observe(viewLifecycleOwner) { historyRecyclerModels ->
            adapter.setHistoryRecyclerModel(historyRecyclerModels)
        }

        val itemDecorator = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(view.context, R.drawable.row_item_divider)!!)
        recyclerView.addItemDecoration(itemDecorator)

        return view
    }
}