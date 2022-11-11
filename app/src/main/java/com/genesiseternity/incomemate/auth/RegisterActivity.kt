package com.genesiseternity.incomemate.auth

import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class RegisterActivity : DaggerAppCompatActivity() {

    @Inject lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var disposableEmail: Disposable
    private lateinit var disposablePassword: Disposable
    private lateinit var disposableConfirmPassword: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.editTextEmailRegister
        password = binding.editTextPasswordRegister
        confirmPassword = binding.editTextPasswordRegisterConfirm
        emailLayout = binding.editTextEmailRegisterLayout
        passwordLayout = binding.editTextPasswordRegisterLayout
        confirmPasswordLayout = binding.editTextPasswordRegisterConfirmLayout

        registerViewModel = ViewModelProvider(this, providerFactory).get(RegisterViewModel::class.java)

        compositeDisposable = CompositeDisposable()

        performInputValidation()
        initRegister()
    }

    private fun performInputValidation()
    {
        registerViewModel.getErrorEmailLiveData().observe(this) { error -> emailLayout.error = error }
        registerViewModel.getErrorPasswordLiveData().observe(this) { error -> passwordLayout.error = error }
        registerViewModel.getErrorConfirmPasswordLiveData().observe(this) { error -> confirmPasswordLayout.error = error }

        disposableEmail = email.textChanges()
            .map { it.toString() }
            .subscribe { registerViewModel.setEmail(it) }

        disposablePassword = password.textChanges()
            .map { it.toString() }
            .subscribe { registerViewModel.setPassword(it) }

        disposableConfirmPassword = confirmPassword.textChanges()
            .map { it.toString() }
            .subscribe { registerViewModel.setConfirmPassword(it) }

        compositeDisposable.addAll(disposableEmail, disposablePassword, disposableConfirmPassword)
    }

    private fun initRegister()
    {
        binding.registerBtn.setOnClickListener { registerViewModel.createUser() }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}