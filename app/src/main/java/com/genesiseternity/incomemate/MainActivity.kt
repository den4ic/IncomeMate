package com.genesiseternity.incomemate

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.genesiseternity.incomemate.databinding.ActivityMainBinding
import com.genesiseternity.incomemate.history.OperationsHistoryFragment
import com.genesiseternity.incomemate.pieChart.PieChartHeadFragment
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.entities.CurrencySettingsEntity
import com.genesiseternity.incomemate.settings.SettingsFragment
import com.genesiseternity.incomemate.utils.LanguageConfig
import com.genesiseternity.incomemate.wallet.WalletFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    companion object {
        var isFirstInit: Boolean = false
    }

    //@Inject lateinit var languageConfig: dagger.Lazy<LanguageConfig>
    //@Inject lateinit var currencySettingsDao: dagger.Lazy<CurrencySettingsDao>

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarTransparent()
        //initSettings()

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(android.Manifest.permission.RECEIVE_SMS), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    @Inject
    fun initSettings(languageConfig: dagger.Lazy<LanguageConfig>, currencySettingsDao: dagger.Lazy<CurrencySettingsDao>)
    {
        compositeDisposable.add(currencySettingsDao.get().getAllCurrencySettingsData()
            .filter {
                if (it.isNotEmpty()) {
                    return@filter true
                } else {
                    compositeDisposable.add(currencySettingsDao.get().insertCurrencySettingsData(CurrencySettingsEntity(
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
                                initialize()
                            },
                            {

                            }))

                    return@filter false
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    //setDefaultCurrencyType(it[0].defaultCurrencyType)

                    //LanguageConfig(this.resources).setLanguage(it[0].defaultLanguageType)
                    languageConfig.get().setLanguage(it[0].defaultLanguageType)
                    languageConfig.get().resources = this.resources
                    languageConfig.get().setLanguage(it[0].defaultLanguageType)

                    initialize()
                },
                {
                    initialize()
                    it.printStackTrace()
                }))
    }

    private fun initialize()
    {
        val idPage = intent.getIntExtra("idNavigationPage", 0)

        if (!isFirstInit)
        {
            isFirstInit = true
            replaceFragment(PieChartHeadFragment())
            binding.bottomNavigationView.selectedItemId = R.id.pie_chart
        }
        else
        {
            if (idPage == 0)
            {
                replaceFragment(WalletFragment())
                binding.bottomNavigationView.selectedItemId = R.id.wallet
            }
            else if (idPage == 1)
            {
                replaceFragment(PieChartHeadFragment())
                binding.bottomNavigationView.selectedItemId = R.id.pie_chart
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener()
        {
            when (it.itemId)
            {
                (R.id.wallet) -> replaceFragment(WalletFragment())
                (R.id.pie_chart) -> replaceFragment(PieChartHeadFragment())
                (R.id.operations_history) -> replaceFragment(OperationsHistoryFragment())
                (R.id.settings) -> replaceFragment(SettingsFragment())
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun replaceFragment(fragment: Fragment)
    {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    //region StatusBar
    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                statusBarColor = Color.TRANSPARENT
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.frameLayout) { _, insets ->
            findViewById<FloatingActionButton>(R.id.frame_layout).setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
    }

    private fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}