package com.genesiseternity.incomemate.auth

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class RegisterViewModel @Inject public constructor(application: Application) : ViewModel() {

    private var authRepository: AuthRepository
    private var errorEmailLiveData: MutableLiveData<String>
    private var errorPasswordLiveData: MutableLiveData<String>
    private var errorConfirmPasswordLiveData: MutableLiveData<String>

    init {
        authRepository = AuthRepository(application)
        errorEmailLiveData = authRepository.getErrorEmailLiveData()
        errorPasswordLiveData = authRepository.getErrorPasswordLiveData()
        errorConfirmPasswordLiveData = authRepository.getErrorConfirmPasswordLiveData()
    }

    fun getErrorEmailLiveData(): MutableLiveData<String> { return errorEmailLiveData }
    fun getErrorPasswordLiveData(): MutableLiveData<String> { return errorPasswordLiveData }
    fun getErrorConfirmPasswordLiveData(): MutableLiveData<String> { return errorConfirmPasswordLiveData }

    fun setEmail(email: String) { authRepository.setEmail(email) }
    fun setPassword(password: String) { authRepository.setPassword(password) }
    fun setConfirmPassword(confirmPassword: String) { authRepository.setConfirmPassword(confirmPassword) }

    //fun createUser(email: String, password: String, confirmPassword: String) { authRepository.createUser(email, password, confirmPassword) }
    fun createUser() { authRepository.createUser() }
}