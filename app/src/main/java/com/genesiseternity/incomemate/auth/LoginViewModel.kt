package com.genesiseternity.incomemate.auth

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor(application: Application) : ViewModel() {

    private val authRepository: AuthRepository
    private val _errorEmailLiveData: MutableLiveData<String>
    private val _errorPasswordLiveData: MutableLiveData<String>

    init {
        authRepository = AuthRepository(application)
        _errorEmailLiveData = authRepository.errorEmailLiveData
        _errorPasswordLiveData = authRepository.errorPasswordLiveData
    }

    val errorEmailLiveData: MutableLiveData<String> = _errorEmailLiveData
    val errorPasswordLiveData: MutableLiveData<String> = _errorPasswordLiveData

    fun setEmail(email: String) { authRepository.setEmail(email) }
    fun setPassword(password: String) { authRepository.setPassword(password) }

    fun initWithoutSignInPage() { authRepository.initWithoutSignInPage() }
    fun initRegisterPage() { authRepository.initRegisterPage() }
    fun signInUser() { authRepository.signInUser() }

}