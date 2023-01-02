package com.genesiseternity.incomemate.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    @Inject lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var email: EditText
    private lateinit var password: EditText

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var disposableEmail: Disposable
    private lateinit var disposablePassword: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.editTextEmailLogin
        password = binding.editTextPasswordLogin
        emailLayout = binding.editTextEmailLoginLayout
        passwordLayout = binding.editTextPasswordLoginLayout

        loginViewModel = ViewModelProvider(this, providerFactory).get(LoginViewModel::class.java)

        compositeDisposable = CompositeDisposable()

        setupPrivacyPolicyLink()
        performInputValidation()
        initWithoutSignInPage()
        initRegisterPage()
        initSignInPage()
    }

    private fun setupPrivacyPolicyLink()
    {
        val privacyPolicy: TextView = binding.privacyPolicy
        val colorBlue: Int = resources.getColor(R.color.blue)
        privacyPolicy.movementMethod = LinkMovementMethod.getInstance()
        privacyPolicy.setLinkTextColor(colorBlue)
    }

    private fun performInputValidation()
    {
        loginViewModel.getErrorEmailLiveData().observe(this) { error -> emailLayout.error = error }
        loginViewModel.getErrorPasswordLiveData().observe(this) { error -> passwordLayout.error = error }

        disposableEmail = email.textChanges()
            .map { it.toString() }
            .subscribe { loginViewModel.setEmail(it) }

        disposablePassword = password.textChanges()
            .map { it.toString() }
            .subscribe { loginViewModel.setPassword(it) }

        compositeDisposable.addAll(disposableEmail, disposablePassword)
    }

    private fun initWithoutSignInPage()
    {
        binding.continueWithoutSignIn.setOnClickListener { loginViewModel.initWithoutSignInPage() }
    }

    private fun initRegisterPage()
    {
        binding.registerBtn.setOnClickListener { loginViewModel.initRegisterPage() }
    }

    private fun initSignInPage()
    {
        binding.continueBtn.setOnClickListener { loginViewModel.signInUser() }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}