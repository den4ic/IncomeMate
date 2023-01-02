package com.genesiseternity.incomemate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.genesiseternity.incomemate.auth.LoginActivity
import com.genesiseternity.incomemate.databinding.ActivityInfoBinding
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class InfoActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityInfoBinding
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoginPage()
    }

    override fun onDestroy()
    {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun initLoginPage()
    {
        compositeDisposable.add(binding.selectLoginBtn.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe {
                startActivity(Intent(this, LoginActivity::class.java))
            })
    }
}