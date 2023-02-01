package com.genesiseternity.incomemate

import android.content.Intent
import android.os.Bundle
import com.genesiseternity.incomemate.auth.LoginActivity
import com.genesiseternity.incomemate.databinding.ActivityInfoBinding
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.entities.CurrencySettingsEntity
import com.genesiseternity.incomemate.settings.Passcode
import com.genesiseternity.incomemate.settings.StateActionPasscode
import com.genesiseternity.incomemate.utils.LanguageConfig
import com.jakewharton.rxbinding4.view.clicks
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class InfoActivity : DaggerAppCompatActivity()
{
    @Inject lateinit var currencySettingsDao: dagger.Lazy<CurrencySettingsDao>
    private lateinit var binding: ActivityInfoBinding
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy()
    {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    @Inject
    fun initSettings(languageConfig: dagger.Lazy<LanguageConfig>, currencySettingsDao: dagger.Lazy<CurrencySettingsDao>)
    {
        compositeDisposable.add(currencySettingsDao.get().getAllCurrencySettingsData()
            .filter {
                if (it.isNotEmpty()) {
                    return@filter true
                } else {
                    compositeDisposable.add(currencySettingsDao.get().insertCurrencySettingsData(
                        CurrencySettingsEntity(
                        0,
                        0,
                        0,
                        0,
                        false,
                        false,
                        0
                        ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                initLoginPage()
                            },
                            {
                                it.printStackTrace()
                            }))

                    return@filter false
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    //setDefaultCurrencyType(it[0].defaultCurrencyType)
                    //LanguageConfig(this.resources).setLanguage(it[0].defaultLanguageType)

                    languageConfig.get().setLanguage(it[0].defaultLanguageType)
                    languageConfig.get().resources = this.resources
                    languageConfig.get().setLanguage(it[0].defaultLanguageType)

                    initLoginPage()
                },
                {
                    initLoginPage()
                    it.printStackTrace()
                }))
    }

    private fun initLoginPage()
    {
        compositeDisposable.add(binding.selectLoginBtn.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe
            {
                compositeDisposable.add(currencySettingsDao.get().getEnabledPasscodeByIdPage()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            if (it) initNewPage(Passcode::class.java) else initNewPage(LoginActivity::class.java)
                        },
                        {
                            it.printStackTrace()
                        }
                    ))
            })
    }

    private fun <T> initNewPage(Klass: Class<T>)
    {
        val intent: Intent = Intent(this, Klass)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("stateActionId", StateActionPasscode.DEFAULT.ordinal)
        startActivity(intent)
    }
}