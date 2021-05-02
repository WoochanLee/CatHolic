package com.woody.cat.holic.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivitySplashBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.MainActivity
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash).apply {
            lifecycleOwner = this@SplashActivity
        }
        viewModel = ViewModelProvider(this, viewModelFactory).get(SplashViewModel::class.java)
            .apply {
                eventStartMainActivity.observeEvent(this@SplashActivity, {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                })
            }
    }
}