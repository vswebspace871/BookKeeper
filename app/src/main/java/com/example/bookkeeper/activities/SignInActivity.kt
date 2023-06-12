package com.example.bookkeeper.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bookkeeper.MyApplication
import com.example.bookkeeper.R
import com.example.bookkeeper.databinding.ActivitySignInBinding
import com.example.bookkeeper.factory.SignInFactory
import com.example.bookkeeper.utils.SharedPrefManager
import com.example.bookkeeper.viewmodels.SignInViewModel


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInViewModel: SignInViewModel
    lateinit var sharedprefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        val vibrate: Animation = AnimationUtils.loadAnimation(this, R.anim.vibrate)

        binding.lifecycleOwner = this
        signInViewModel = ViewModelProvider(
            this,
            SignInFactory(MyApplication.getUserRepository(this))
        )[SignInViewModel::class.java]

        binding.signInViewModel = signInViewModel

        /** setting context in shared Preference */
        sharedprefManager = SharedPrefManager(this)

        if (!TextUtils.isEmpty(sharedprefManager.isLoggedIn())) {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        }


        signInViewModel.isUserLoggedIn.observe(this) { isUserLoggedIn ->
            if (isUserLoggedIn) {
                sharedprefManager.setLoggedIn(signInViewModel.username.value.toString())
                var intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.tvWarning.visibility = View.VISIBLE
                binding.tvWarning.animation = vibrate
            }
        }

    }

}