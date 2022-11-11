package com.genesiseternity.incomemate.auth

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor(application: Application) : ViewModel() {

    private var authRepository: AuthRepository
    private var errorEmailLiveData: MutableLiveData<String>
    private var errorPasswordLiveData: MutableLiveData<String>

    init {
        authRepository = AuthRepository(application)
        errorEmailLiveData = authRepository.getErrorEmailLiveData()
        errorPasswordLiveData = authRepository.getErrorPasswordLiveData()
    }

    fun getErrorEmailLiveData(): MutableLiveData<String> { return errorEmailLiveData }
    fun getErrorPasswordLiveData(): MutableLiveData<String> { return errorPasswordLiveData }

    fun setEmail(email: String) { authRepository.setEmail(email) }
    fun setPassword(password: String) { authRepository.setPassword(password) }

    fun initWithoutSignInPage() { authRepository.initWithoutSignInPage() }
    fun initRegisterPage() { authRepository.initRegisterPage() }
    fun signInUser() { authRepository.signInUser() }

}