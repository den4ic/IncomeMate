package com.genesiseternity.incomemate

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.scaleMatrix
import androidx.core.text.isDigitsOnly
import androidx.core.view.size
import com.genesiseternity.incomemate.auth.LoginActivity
import com.genesiseternity.incomemate.databinding.ActivityPasscodeBinding
import com.genesiseternity.incomemate.room.entities.CurrencySettingsEntity
import com.genesiseternity.incomemate.utils.isNumeric
import com.genesiseternity.incomemate.utils.removeLastChar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.channels.ticker
import java.util.concurrent.TimeUnit

class Passcode : AppCompatActivity()
{
    private lateinit var binding: ActivityPasscodeBinding
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputPasscode()
    }

    override fun onDestroy()
    {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    val MAX_NUMBER_ATTEMMPTS_LOGIN = 5
    val MAX_VALUE_PASSCODE: Int = 4
    val DEFAULT_DELAY_INPUT: Long = 2
    var inputPasscode: String = ""
    var validPasscode: String = "1111"

    var counterAttemptLogin = 0
    var disabledInput: Boolean = false

    private fun setTintAllDiode(color: ColorStateList)
    {
        for (i in 0 until binding.diodeList.childCount) {
            binding.diodeList.getChildAt(i).backgroundTintList = color
        }
    }

    private fun inputPasscode()
    {
        val redDiode = AppCompatResources.getColorStateList(this, R.color.red)
        val greenDiode = AppCompatResources.getColorStateList(this, R.color.green)
        val blueDiode = AppCompatResources.getColorStateList(this, R.color.blue)
        val disableDiode = AppCompatResources.getColorStateList(this, R.color.lightGray)

        val diodeList = binding.diodeList
        val gridBtnList = binding.gridBtnList
        val childCountBtnList = gridBtnList.childCount-1

        for (i in 0..childCountBtnList)
        {
            gridBtnList.getChildAt(i).setOnClickListener {

                if (it is AppCompatButton && !disabledInput)
                {
                    if (it.text.isDigitsOnly() && inputPasscode.length < MAX_VALUE_PASSCODE) {
                        inputPasscode += it.text

                        diodeList.getChildAt(inputPasscode.length-1).backgroundTintList = blueDiode

                        animateInputDiode(diodeList, inputPasscode.length-1)

                        if (inputPasscode == validPasscode)
                        {
                            setTintAllDiode(greenDiode)

                            val intent: Intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                        else if (inputPasscode.length == validPasscode.length)
                        {
                            setTintAllDiode(redDiode)

                            disabledInput = true
                            counterAttemptLogin++

                            if (counterAttemptLogin == MAX_NUMBER_ATTEMMPTS_LOGIN)
                            {
                                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                                    .setTitle("Слишком много попыток")
                                    .setMessage("Войдите в приложение заново")
                                    .setCancelable(false)
                                    .setPositiveButton("Ок") { dialogInterface, i ->
                                        val intent: Intent = Intent(this, LoginActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                        dialogInterface.dismiss()
                                    }
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.show()
                            }

                            for (j in 0 until binding.diodeList.childCount)
                            {
                                animateInputDiode(diodeList, j)
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
                    }
                    else
                    {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                            .setTitle("Внимание")
                            .setMessage("Если вы выйдите, нужно будет авторизоваться заново в приложении")
                            .setCancelable(true)
                            .setPositiveButton("Подтвердить") { dialogInterface, i ->
                                val intent: Intent = Intent(this, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                dialogInterface.dismiss()
                            }
                            .setNegativeButton("Отмена") { dialogInterface, i ->
                                dialogInterface.cancel()
                            }

                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.show()
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