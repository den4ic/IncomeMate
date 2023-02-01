package com.genesiseternity.incomemate.auth

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class RegisterViewModel @Inject public constructor(application: Application) : ViewModel() {

    private val authRepository: AuthRepository
    private val _errorEmailLiveData: MutableLiveData<String>
    private val _errorPasswordLiveData: MutableLiveData<String>
    private val _errorConfirmPasswordLiveData: MutableLiveData<String>

    init {
        authRepository = AuthRepository(application)
        _errorEmailLiveData = authRepository.errorEmailLiveData
        _errorPasswordLiveData = authRepository.errorPasswordLiveData
        _errorConfirmPasswordLiveData = authRepository.errorConfirmPasswordLiveData
    }

    val errorEmailLiveData: MutableLiveData<String> get() = _errorEmailLiveData
    val errorPasswordLiveData: MutableLiveData<String> get() = _errorPasswordLiveData
    val errorConfirmPasswordLiveData: MutableLiveData<String> get() = _errorConfirmPasswordLiveData

    fun setEmail(email: String) { authRepository.setEmail(email) }
    fun setPassword(password: String) { authRepository.setPassword(password) }
    fun setConfirmPassword(confirmPassword: String) { authRepository.setConfirmPassword(confirmPassword) }

    //fun createUser(email: String, password: String, confirmPassword: String) { authRepository.createUser(email, password, confirmPassword) }
    fun createUser() { authRepository.createUser() }
}