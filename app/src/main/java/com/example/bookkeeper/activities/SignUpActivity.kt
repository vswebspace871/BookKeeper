package com.example.bookkeeper.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bookkeeper.MyApplication
import com.example.bookkeeper.R
import com.example.bookkeeper.databinding.ActivitySignUpBinding
import com.example.bookkeeper.factory.SignUpFactory
import com.example.bookkeeper.utils.SharedPrefManager
import com.example.bookkeeper.viewmodels.SignUpViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    var currentBalance: Double = 0.0
    lateinit var sharedprefManager: SharedPrefManager
    private val TAG = "TAG"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        /** generate random account number from current timestamp */
        val accountNumber = generateRandomAccNumber()

        signUpViewModel = ViewModelProvider(
            this,
            SignUpFactory(MyApplication.getUserRepository(this))
        )[SignUpViewModel::class.java]

        binding.signUpViewModel = signUpViewModel

        binding.lifecycleOwner = this

        /** setting current balance in ViewModel*/
        signUpViewModel.accountBalance.value = currentBalance


        /** setting account number in viewModel*/
        signUpViewModel.accountNumber.value = accountNumber

        binding.etUsername.setOnFocusChangeListener { _, b ->
            if (!b) {
                signUpViewModel.findDuplicateUsername(binding.etUsername.text.toString())
                /*if (!signUpViewModel.duplicateUserName.hasActiveObservers()) {
                    checkDuplicateUser()
                }*/
            }
        }

        binding.tvSignin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        }
        /** send Username Edittext value to ViewModel to check Duplicate */
        binding.etUsername.afterTextChangedDelayed {
            signUpViewModel.findDuplicateUsername(binding.etUsername.text.toString())
        }

        /** Observing Username if not exist in DB show red color Bordered EditText*/
        signUpViewModel.duplicateUserName.observe(this, Observer { isuserExist ->
            if (isuserExist) {
                //signUpViewModel.duplicateUsername = true
                binding.etUsername.error = "Username Already Exist, Choose Different"
                binding.etUsername.setBackgroundResource(R.drawable.edittext_red_border)
            } else {
                //signUpViewModel.duplicateUsername = false
                binding.etUsername.setBackgroundResource(R.drawable.edittext_border)
            }
        })

        /** send Password Edittext length to ViewModel */
        binding.etPassword.afterTextChangedDelayed { password ->
            signUpViewModel.checkPasswordLength(password)
        }
        /** Observing password length if less than 6 show red color Bordered EditText */
        signUpViewModel.isSmallPassword.observe(this, Observer { isSmallPassword ->
            if (isSmallPassword) {
                binding.etPassword.setBackgroundResource(R.drawable.edittext_red_border)
                binding.etPassword.error = "Password length should exact 4"
            } else {
                binding.etPassword.setBackgroundResource(R.drawable.edittext_border)
            }
        })


        /** If Account created Successfully show Toast Message*/
        signUpViewModel.result.observe(this, Observer { isInserted ->
            if (isInserted) {
                /** saving username in shared Preference */
                sharedprefManager = SharedPrefManager(this)
                sharedprefManager.setLoggedIn(signUpViewModel.username.value.toString())
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                Toast.makeText(
                    this@SignUpActivity, "Account Created Successfully",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        /*binding.btnRegister.setOnClickListener {
            if (binding.etFullname.text.isEmpty() ||
                binding.etUsername.text.isEmpty() ||
                binding.etPassword.text.isEmpty()
            ) {
                Toast.makeText(
                    this@SignUpActivity, "All Field Must Be Filled",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                fullname = binding.etFullname.text.toString()
                userName = binding.etUsername.text.toString()
                password = binding.etPassword.text.toString()

                    signUpViewModel.createUser(User(null, fullname, userName, password))
                    startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                    Toast.makeText(
                        this@SignUpActivity, "Account Created Successfully",
                        Toast.LENGTH_LONG
                    ).show()

            }
        }*/

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateRandomAccNumber(): String {
        var random =
            kotlin.math.abs(Random(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).nextLong())
                .toString()
        var length = random.length
        return random.substring(length - 8)
    }


    /* private fun checkDuplicateUser() {
         signUpViewModel.duplicateUserName.observe(this, Observer {isuserExist->
             if (isuserExist){
                 Toast.makeText(
                     this@SignUpActivity, "Username already Exist, Choose Different",
                     Toast.LENGTH_LONG
                 ).show()
                 binding.etUsername.setBackgroundResource(R.drawable.edittext_red_border)
             } else {
                 binding.etUsername.setBackgroundResource(R.drawable.edittext_border)
             }
         })
     }*/
    /** */
    fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(1000, 1500) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        afterTextChanged.invoke(editable.toString())
                    }
                }.start()
            }
        })
    }
}