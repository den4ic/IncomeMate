package com.genesiseternity.incomemate.settings

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.auth.LoginActivity
import com.genesiseternity.incomemate.databinding.ActivityPasscodeBinding
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.utils.removeLastChar
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.KClass

enum class StateActionPasscode
{
    ENABLE, CHANGE, DISABLE, DEFAULT
}

enum class PageSequenceNumber
{
    FIRST, SECOND, THIRD
}

class Passcode : DaggerAppCompatActivity()
{
    companion object {
        var isEnabledPasscode: Boolean = false
        var validPasscode: String = "0000"
        var pageSequenceNumber: Int = PageSequenceNumber.FIRST.ordinal
    }

    @Inject lateinit var currencySettingsDao: dagger.Lazy<CurrencySettingsDao>

    private lateinit var binding: ActivityPasscodeBinding
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    var stateActionId: Int = StateActionPasscode.DEFAULT.ordinal

    val MAX_NUMBER_ATTEMMPTS_LOGIN = 5
    val MAX_VALUE_PASSCODE: Int = 4
    val DEFAULT_DELAY_INPUT: Long = 2
    var inputPasscode: String = ""

    var counterAttemptLogin = 0
    var disabledInput: Boolean = false

    val diodeList by lazy { binding.diodeList }

    val redDiode by lazy { AppCompatResources.getColorStateList(this, R.color.red) }
    val greenDiode by lazy { AppCompatResources.getColorStateList(this, R.color.green) }
    val blueDiode by lazy { AppCompatResources.getColorStateList(this, R.color.blue) }
    val disableDiode by lazy { AppCompatResources.getColorStateList(this, R.color.lightGray) }

    private val textEnterPasscode: String = "Введите код-пароль"
    private val textConfirmNewPasscode: String = "Подтвердите свой новый код-пароль"
    private val textEnterOldPasscode: String = "Введите старый код-пароль"
    private val textEnterNewPasscode: String = "Введите новый код-пароль"
    private val textTryAgain: String = "Код-пароли не совпали. Повторите попытку"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stateActionId = intent.getIntExtra("stateActionId", StateActionPasscode.DEFAULT.ordinal)


        // ENABLE Введите код-пароль - Подтвердите свой новый код-пароль
        // CHANGE Введите старый код-пароль - Введите новый код-пароль - Подтвердите свой новый код-пароль / Код-пароли не совпали. Повторите попытку
        // DISABLE Введите код-пароль isPasscode
        // DEFAULT Введите код-пароль isNotPasscode

        pageSequenceNumber = PageSequenceNumber.FIRST.ordinal

        when (StateActionPasscode.values()[stateActionId])
        {
            StateActionPasscode.ENABLE -> {
                binding.titleTextPasscode.text = textEnterPasscode
                //isEnabledPasscode = true
            }
            StateActionPasscode.CHANGE -> {
                binding.titleTextPasscode.text = textEnterOldPasscode
                //isEnabledPasscode = true
            }
            StateActionPasscode.DISABLE -> {
                binding.titleTextPasscode.text = textEnterPasscode
            }
            StateActionPasscode.DEFAULT -> {
                binding.titleTextPasscode.text = textEnterPasscode
                //isEnabledPasscode = true
            }
        }

        /*
        compositeDisposable.add(currencySettingsDao.get().updateEnabledPasscode(0, isEnabledPasscode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {

                },
                {
                    it.printStackTrace()
                }
            ))

        compositeDisposable.add(currencySettingsDao.get().getEnabledPasscodeByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    isEnabledPasscode = it
                },
                {
                    it.printStackTrace()
                }
            ))

        if (!isEnabledPasscode)
        {
            initNewPage(MainActivity::class.java)
            return
        }

         */

        compositeDisposable.add(currencySettingsDao.get().getPasscodeByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    validPasscode = it.toString()
                    inputPasscode()
                },
                {
                    it.printStackTrace()
                    inputPasscode()
                }
            ))
    }

    override fun onDestroy()
    {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun inputPasscode()
    {
        val gridBtnList = binding.gridBtnList

        for (i in 0 until gridBtnList.childCount)
        {
            gridBtnList.getChildAt(i).setOnClickListener {

                if (it is AppCompatButton && !disabledInput)
                {
                    if (it.text.isDigitsOnly() && inputPasscode.length < MAX_VALUE_PASSCODE)
                    {
                        inputPasscode += it.text

                        diodeList.getChildAt(inputPasscode.length-1).backgroundTintList = blueDiode
                        animateInputDiode(diodeList, inputPasscode.length-1)

                        if (inputPasscode.length == MAX_VALUE_PASSCODE)
                        {
                            when (stateActionId)
                            {
                                StateActionPasscode.ENABLE.ordinal ->
                                {
                                    if (pageSequenceNumber == PageSequenceNumber.FIRST.ordinal)
                                    {
                                        validPasscode = inputPasscode
                                        binding.titleTextPasscode.text = textConfirmNewPasscode
                                        setTintAllDiode(greenDiode, true)
                                        setBlockInputTimer(PageSequenceNumber.SECOND)
                                    }
                                    else if (pageSequenceNumber == PageSequenceNumber.SECOND.ordinal)
                                    {
                                        if (inputPasscode == validPasscode)
                                        {
                                            compositeDisposable.add(currencySettingsDao.get().updatePasscode(0, validPasscode.toInt())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(
                                                    {
                                                        isEnabledPasscode = true

                                                        compositeDisposable.add(currencySettingsDao.get().updateEnabledPasscode(0, isEnabledPasscode)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(
                                                                {
                                                                    setTintAllDiode(greenDiode)
                                                                    pageSequenceNumber = PageSequenceNumber.FIRST.ordinal
                                                                    initNewPage(MainActivity::class.java)
                                                                },
                                                                {
                                                                    it.printStackTrace()
                                                                }
                                                            ))
                                                    },
                                                    {
                                                        it.printStackTrace()
                                                    }
                                                ))
                                        }
                                        else if (inputPasscode.length == validPasscode.length)
                                        {
                                            wrongInputPasscode()
                                        }
                                    }
                                }
                                StateActionPasscode.CHANGE.ordinal ->
                                {
                                    if (pageSequenceNumber == PageSequenceNumber.FIRST.ordinal)
                                    {
                                        if (inputPasscode == validPasscode)
                                        {
                                            binding.titleTextPasscode.text = textEnterNewPasscode
                                            setTintAllDiode(greenDiode, true)
                                            setBlockInputTimer(PageSequenceNumber.SECOND)
                                        }
                                        else if (inputPasscode.length == validPasscode.length)
                                        {
                                            wrongInputPasscode()
                                        }
                                    }
                                    else if (pageSequenceNumber == PageSequenceNumber.SECOND.ordinal)
                                    {
                                        validPasscode = inputPasscode
                                        binding.titleTextPasscode.text = textConfirmNewPasscode
                                        setTintAllDiode(greenDiode, true)
                                        setBlockInputTimer(PageSequenceNumber.THIRD)
                                    }
                                    else if (pageSequenceNumber == PageSequenceNumber.THIRD.ordinal)
                                    {
                                        if (inputPasscode == validPasscode)
                                        {
                                            compositeDisposable.add(currencySettingsDao.get().updatePasscode(0, validPasscode.toInt())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(
                                                    {
                                                        isEnabledPasscode = true

                                                        compositeDisposable.add(currencySettingsDao.get().updateEnabledPasscode(0, isEnabledPasscode)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(
                                                                {
                                                                    setTintAllDiode(greenDiode)
                                                                    pageSequenceNumber = PageSequenceNumber.FIRST.ordinal
                                                                    initNewPage(MainActivity::class.java)
                                                                },
                                                                {
                                                                    it.printStackTrace()
                                                                }
                                                            ))
                                                    },
                                                    {
                                                        it.printStackTrace()
                                                    }
                                                ))
                                        }
                                        else if (inputPasscode.length == validPasscode.length)
                                        {
                                            wrongInputPasscode()
                                        }
                                    }
                                }
                                StateActionPasscode.DISABLE.ordinal ->
                                {
                                    if (inputPasscode == validPasscode)
                                    {
                                        setTintAllDiode(greenDiode)
                                        isEnabledPasscode = false

                                        compositeDisposable.add(currencySettingsDao.get().updateEnabledPasscode(0, isEnabledPasscode)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                {
                                                    initNewPage(MainActivity::class.java)
                                                },
                                                {
                                                    it.printStackTrace()
                                                }
                                            ))

                                    }
                                    else if (inputPasscode.length == validPasscode.length)
                                    {
                                        wrongInputPasscode()
                                    }
                                }
                                StateActionPasscode.DEFAULT.ordinal ->
                                {
                                    if (inputPasscode == validPasscode)
                                    {
                                        setTintAllDiode(greenDiode)
                                        initNewPage(MainActivity::class.java)
                                    }
                                    else if (inputPasscode.length == validPasscode.length)
                                    {
                                        wrongInputPasscode()
                                    }
                                }
                            }
                        }
                    }
                    else if (!it.text.isDigitsOnly())
                    {
                        alertAboutExit()
                    }
                }
                else if (it is TableRow && !disabledInput)
                {
                    if (inputPasscode.length > 0 )
                    {
                        inputPasscode = String.removeLastChar(inputPasscode)
                        diodeList.getChildAt(inputPasscode.length).backgroundTintList = disableDiode
                    }
                }
            }
        }
    }

    private fun wrongInputPasscode()
    {
        setTintAllDiode(redDiode, true)
        disabledInput = true
        counterAttemptLogin++

        if (counterAttemptLogin == MAX_NUMBER_ATTEMMPTS_LOGIN)
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setTitle("Слишком много попыток")
                .setMessage("Войдите в приложение заново")
                .setCancelable(false)
                .setPositiveButton("Ок") { dialogInterface, i ->

                    isEnabledPasscode = false

                    compositeDisposable.add(currencySettingsDao.get().updateEnabledPasscode(0, isEnabledPasscode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                initNewPage(LoginActivity::class.java)
                                dialogInterface.dismiss()
                            },
                            {
                                it.printStackTrace()
                            }
                        ))
                }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        compositeDisposable.add(Observable.timer(DEFAULT_DELAY_INPUT, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setTintAllDiode(disableDiode)
                inputPasscode = ""
                disabledInput = false
            }
        )
    }

    private fun setBlockInputTimer(enum: Enum<PageSequenceNumber>)
    {
        compositeDisposable.add(Observable.timer(DEFAULT_DELAY_INPUT, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setTintAllDiode(disableDiode)
                inputPasscode = ""
                disabledInput = false

                pageSequenceNumber = enum.ordinal
            }
        )
    }

    private fun alertAboutExit()
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("Внимание")
            .setMessage("Если вы выйдите, нужно будет авторизоваться заново в приложении")
            .setCancelable(true)
            .setPositiveButton("Подтвердить") { dialogInterface, i ->

                isEnabledPasscode = false

                compositeDisposable.add(currencySettingsDao.get().updateEnabledPasscode(0, isEnabledPasscode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            initNewPage(LoginActivity::class.java)
                            dialogInterface.dismiss()
                        },
                        {
                            it.printStackTrace()
                        }
                    ))
            }
            .setNegativeButton("Отмена") { dialogInterface, i ->
                dialogInterface.cancel()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun <T> initNewPage(Klass: Class<T>)
    {
        val intent: Intent = Intent(this, Klass)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun setTintAllDiode(color: ColorStateList, isAnim: Boolean = false)
    {
        for (i in 0 until diodeList.childCount) {
            diodeList.getChildAt(i).backgroundTintList = color
            if (isAnim)
                animateInputDiode(diodeList, i)
        }
    }

    private fun animateInputDiode(diodeList: LinearLayout, idChild: Int)
    {
        diodeList.getChildAt(idChild).animate().apply {
            scaleXBy(0.0f)
            scaleYBy(0.0f)
            duration = 100
            scaleX(0.3f)
            scaleY(0.3f)
            interpolator = AccelerateInterpolator()

        }.withEndAction {
            diodeList.getChildAt(idChild).animate().apply {
                duration = 100
                scaleXBy(0.7f)
                scaleYBy(0.7f)
            }
        }.start()
    }
}