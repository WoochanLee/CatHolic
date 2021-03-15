package com.woody.cat.holic.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivitySplashBinding
import com.woody.cat.holic.presentation.main.MainActivity

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        viewModel =
            ViewModelProvider(this, SplashViewModelFactory()).get(SplashViewModel::class.java)
                .apply {
                    eventStartMainActivity.observe(this@SplashActivity, {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    })
                }
    }
}