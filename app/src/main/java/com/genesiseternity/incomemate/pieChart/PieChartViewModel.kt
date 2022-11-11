package com.genesiseternity.incomemate.pieChart

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class PieChartViewModel @Inject constructor(application: Application) : ViewModel() {

    init {
        Log.d("PieChartViewModel", "OK IS IT" + application)
    }
}