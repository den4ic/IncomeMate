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

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

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

        registerViewModel = ViewModelProvider(this, providerFactory)[RegisterViewModel::class.java]

        performInputValidation()
        initRegister()
    }

    private fun performInputValidation()
    {
        registerViewModel.errorEmailLiveData.observe(this) { error -> emailLayout.error = error }
        registerViewModel.errorPasswordLiveData.observe(this) { error -> passwordLayout.error = error }
        registerViewModel.errorConfirmPasswordLiveData.observe(this) { error -> confirmPasswordLayout.error = error }

        compositeDisposable.add(email.textChanges()
            .map { it.toString() }
            .subscribe { registerViewModel.setEmail(it) })

        compositeDisposable.add(password.textChanges()
            .map { it.toString() }
            .subscribe { registerViewModel.setPassword(it) })

        compositeDisposable.add(confirmPassword.textChanges()
            .map { it.toString() }
            .subscribe { registerViewModel.setConfirmPassword(it) })
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