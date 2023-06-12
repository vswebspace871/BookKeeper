package com.example.bookkeeper.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bookkeeper.MyApplication
import com.example.bookkeeper.R
import com.example.bookkeeper.databinding.ActivityAddTransactionBinding
import com.example.bookkeeper.factory.SignUpFactory
import com.example.bookkeeper.utils.SharedPrefManager
import com.example.bookkeeper.viewmodels.SignUpViewModel

class AddTransactionActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddTransactionBinding
    private lateinit var signUpViewModel: SignUpViewModel
    lateinit var sharedprefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_transaction)
        binding.lifecycleOwner = this
        signUpViewModel = ViewModelProvider(
            this,
            SignUpFactory(MyApplication.getUserRepository(this))
        )[SignUpViewModel::class.java]

        binding.signUpViewModel = signUpViewModel

        /** Setting UserId in ViewModel to get All transactions, ID will be SET by itself in ViewModel */
        sharedprefManager = SharedPrefManager(this)
        signUpViewModel.findDuplicateUsername(sharedprefManager.isLoggedIn())

        /** show inserted transactionmsg and start Intent*/
        signUpViewModel.result.observe(this, Observer {isTransactionInserted ->
            if (isTransactionInserted){
                var intent = Intent(this@AddTransactionActivity, MainActivity::class.java)
                intent.putExtra("amount", signUpViewModel.amount.value)
                intent.putExtra("paymentType", signUpViewModel.radioName)
                startActivity(intent)
                Toast.makeText(this, "Transaction Inserted Successfully", Toast.LENGTH_LONG).show()
            }
        })
        setSupportActionBar(binding.toolbarTransaction)
        /** Back Arrow function on Toolbar*/
        binding.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.addCallback()
        }






    }


}

private fun OnBackPressedDispatcher.addCallback() {
    onBackPressed()
}
