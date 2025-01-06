package com.example.containertracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.containertracker.R
import com.example.containertracker.data.user.models.User
import com.example.containertracker.ui.login.LoginActivity
import com.example.containertracker.ui.main.MainActivity
import com.example.containertracker.utils.DateUtil
import com.example.containertracker.utils.PreferenceUtils
import org.threeten.bp.LocalDateTime

class SplashActivity : AppCompatActivity() {
    private val user by lazy {
        PreferenceUtils.get<User>(PreferenceUtils.USER_PREFERENCE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (user != null)
                checkExpiredDate()
            else
                goToLogin()
        }, 2000)
    }

    private fun checkExpiredDate() {
        val expiredDate = DateUtil.formatStringToDateTime(user?.expiredDate)
        val expiredDateLeft = DateUtil.expiredDateLeft(expiredDate ?: LocalDateTime.now())
        if (expiredDateLeft >= 0) goToLogin() else goToMainActivity()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}