package com.genesiseternity.incomemate

import android.app.Application
import android.util.Log
import com.genesiseternity.incomemate.auth.AuthRepository
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.wallet.WalletViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

class AuthRepositoryTest {

    //lateinit var authRepository: AuthRepository
    val application = mock<Application>()

    @AfterEach
    fun afterEach() {
        Mockito.reset(application)
        //Mockito.reset(authRepository)
    }

    @BeforeEach
    fun beforeEach() {

    }

    @Test
    fun shouldReturnNameApp() {

        var authRepository = AuthRepository(
            application = application
        )



        val expected = authRepository.javaClass
        val actual = authRepository.javaClass

        Assertions.assertEquals(expected, actual)
    }
}