package com.genesiseternity.incomemate

import android.util.Log
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class DisposableManager()
{
    companion object Dispose
    {
        //fun create(): DisposableManager = DisposableManager()
        private val TAG: String = DisposableManager::class.simpleName.toString()
        private lateinit var compositeDisposable: CompositeDisposable

        fun add(disposable: Disposable)
        {
            Log.d(TAG, "add disposable")
            getCompositeDisposable().add(disposable)
        }

        fun addAll(vararg disposable: Disposable)
        {
            Log.d(TAG, "add all disposable")
            getCompositeDisposable().addAll(*disposable)
        }

        fun dispose()
        {
            Log.d(TAG, "dispose disposable")
            getCompositeDisposable().dispose()
        }

        private fun getCompositeDisposable(): CompositeDisposable
        {
            if (compositeDisposable.isDisposed)
            {
                compositeDisposable = CompositeDisposable()
            }
            return compositeDisposable
        }
    }
}