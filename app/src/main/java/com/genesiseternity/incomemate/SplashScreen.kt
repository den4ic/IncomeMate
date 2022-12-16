package com.genesiseternity.incomemate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent: Intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
        finish()
    }
}