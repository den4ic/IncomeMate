package com.genesiseternity.incomemate.auth

import android.app.Application
import android.content.Intent
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.genesiseternity.incomemate.PremiumSubscriptionActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class AuthRepository constructor(private var application: Application) {

    private val firebaseAuth: FirebaseAuth
    private val _errorEmailLiveData: MutableLiveData<String>
    private val _errorPasswordLiveData: MutableLiveData<String>
    private val _errorConfirmPasswordLiveData: MutableLiveData<String>
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    private val textErrorLogin = "Пожалуйста, заполните адрес электронной почты."
    private val textErrorIncorrectLogin = "Некорректный адрес электронной почты."
    private val textErrorNonLogin = "Нет записи пользователя, соответствующей этому идентификатору. Либо исправьте свой адрес электронной почты и/или пароль."
    private val textErrorPassword = "Пожалуйста, заполните пароль."
    private val shouldPasswordLength = 6
    private val textErrorShouldPassword = "Пароль должен быть не менее 6 символов."
    private val textErrorBanUser = "Учетная запись пользователя заблокирована."

    private val textErrorAlreadyUseLogin: String = "Адрес электронной почты уже используется другой учетной записью."
    private val textErrorConfirmPassword: String = "Пожалуйста, повторите пароль."

    private val EMPTY_STRING = ""

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        _errorEmailLiveData = MutableLiveData<String>()
        _errorPasswordLiveData = MutableLiveData<String>()
        _errorConfirmPasswordLiveData = MutableLiveData<String>()
    }

    val errorEmailLiveData: MutableLiveData<String> get() = _errorEmailLiveData
    val errorPasswordLiveData: MutableLiveData<String> get() = _errorPasswordLiveData
    val errorConfirmPasswordLiveData: MutableLiveData<String> get() = _errorConfirmPasswordLiveData

    fun setEmail(email: String) { this.email = email }
    fun setPassword(password: String) { this.password = password }
    fun setConfirmPassword(confirmPassword: String) { this.confirmPassword = confirmPassword }

    fun initWithoutSignInPage()
    {
        val intent = Intent(application, PremiumSubscriptionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    fun initRegisterPage()
    {
        //application.startActivity(Intent(application, RegisterActivity::class))
        val intent = Intent(application, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    fun signInUser()
    {
        if (isValidateEmail() && isValidatePassword()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                //.addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        initWithoutSignInPage()
                    }
                    else
                    {
                        //Log.w(TAG, "createUserWithEmail:failure", it.getException())
                        val errorCode: String = (it.exception as FirebaseAuthException).errorCode

                        if (errorCode.equals("ERROR_USER_DISABLED"))
                        {
                            _errorEmailLiveData.postValue(textErrorBanUser)
                        }
                        else
                        {
                            _errorEmailLiveData.postValue(textErrorNonLogin)
                        }
                    }
                }
        }
    }

    private fun isValidateEmail() : Boolean
    {
        if (email.isEmpty())
        {
            _errorEmailLiveData.postValue(textErrorLogin)
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            _errorEmailLiveData.postValue(textErrorIncorrectLogin)
            return false
        }
        else
        {
            _errorEmailLiveData.postValue(EMPTY_STRING)
        }
        return true
    }

    private fun isValidatePassword(): Boolean
    {
        if (password.isEmpty())
        {
            _errorPasswordLiveData.postValue(textErrorPassword)
            return false
        }
        else if (password.length < shouldPasswordLength)
        {
            _errorPasswordLiveData.postValue(textErrorShouldPassword)
            return false
        }
        else
        {
            _errorPasswordLiveData.postValue(EMPTY_STRING)
        }
        return true
    }

    //region Register
    fun createUser()
    {
        if (isValidateEmail() && isValidatePassword() && isValidateConfirmPassword())
        {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        //Log.d(TAG, "createUserWithEmail:success")
                        //Log.w(TAG, "createUserWithEmail uid: " + task.getResult().getUser().getUid())
                        initWithoutSignInPage()
                        //Intent intent = new Intent(RegisterActivity.this, PremiumSubscriptionActivity.class)
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //startActivity(intent)
                    } else
                    {
                        //Log.w(TAG, "createUserWithEmail:failure", task.getException())
                        _errorEmailLiveData.postValue(textErrorAlreadyUseLogin)
                    }
                }
        }
    }

    private fun isValidateConfirmPassword(): Boolean
    {
        if (confirmPassword.isEmpty() || !confirmPassword.equals(password))
        {
            _errorConfirmPasswordLiveData.postValue(textErrorConfirmPassword)
            return false
        }
        else
        {
            _errorConfirmPasswordLiveData.postValue(EMPTY_STRING)
        }
        return true
    }
    //endregion
}