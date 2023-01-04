package com.genesiseternity.incomemate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.genesiseternity.incomemate.databinding.ActivityPremiumSubscriptionBinding
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

class PremiumSubscriptionActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityPremiumSubscriptionBinding
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumSubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMainMenuPage()
    }

    private fun initMainMenuPage()
    {
        val disposableMainMenuBtn: Disposable = binding.mainMenuBtn.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe {
                initMainActivity()
            }

        val disposableSubPlanBtn: Disposable = binding.selectFreeSubPlanBtn.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe {
                initMainActivity()
            }

        compositeDisposable.addAll(disposableMainMenuBtn, disposableSubPlanBtn)
    }

    private fun initMainActivity()
    {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onDestroy()
    {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}