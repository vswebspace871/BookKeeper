package com.example.bookkeeper.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.bookkeeper.MyApplication
import com.example.bookkeeper.R
import com.example.bookkeeper.adapter.ViewPagerAdapter
import com.example.bookkeeper.databinding.ActivityMainBinding
import com.example.bookkeeper.factory.SignUpFactory
import com.example.bookkeeper.fragments.ThirdFragment
import com.example.bookkeeper.fragments.TransactionFragment
import com.example.bookkeeper.utils.SharedPrefManager
import com.example.bookkeeper.viewmodels.SignUpViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var sharedprefManager: SharedPrefManager
    private lateinit var signUpViewModel: SignUpViewModel
    lateinit var username: String
    lateinit var viewPager: ViewPager2

    var transactionAmount: String? = ""
    var transactionType: String? = ""
    private val TAG = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedprefManager = SharedPrefManager(this)
        username = sharedprefManager.isLoggedIn()

        signUpViewModel = ViewModelProvider(
            this,
            SignUpFactory(MyApplication.getUserRepository(this))
        )[SignUpViewModel::class.java]

        binding.signUpViewModel = signUpViewModel

       /* signUpViewModel.getTransactions()
        signUpViewModel.tenTransactionsList.observe(this, Observer {
            Log.d(TAG, "transaction List: $it")
        })*/


        if (intent.hasExtra("amount") && intent.hasExtra("paymentType")) {
            transactionAmount = intent.getStringExtra("amount")
            transactionType = intent.getStringExtra("paymentType")
            if (transactionType == "credit") {
                signUpViewModel.getUserByUsername(username)
                signUpViewModel.accountBalance.observe(this, Observer { accountBalance ->
                    val rupeeSymbol = "₹ "
                    var totalAmount = accountBalance + transactionAmount!!.toDouble()
                    binding.tvCurrentBalance.text = rupeeSymbol + totalAmount
                    signUpViewModel.updateBalance(totalAmount, signUpViewModel.userId)
                })
            }

        }

        if (intent.hasExtra("amount") && intent.hasExtra("paymentType")) {
            transactionAmount = intent.getStringExtra("amount")
            transactionType = intent.getStringExtra("paymentType")
            Log.d(TAG, "onCreate: debit amount $transactionAmount")
            if (transactionType == "debit") {
                signUpViewModel.getUserByUsername(username)
                signUpViewModel.accountBalance.observe(this, Observer { accountBalance ->
                    val rupeeSymbol = "₹ "
                    var totalAmount = accountBalance - transactionAmount!!.toDouble()
                    binding.tvCurrentBalance.text = rupeeSymbol + totalAmount
                    Log.d(TAG, "onCreate: userId ${signUpViewModel.userId}")
                    signUpViewModel.updateBalance(totalAmount, signUpViewModel.userId)
                })
            }

        } else {
            /** get user all info by username and save in viewModel */
            signUpViewModel.getUserByUsername(username)
            signUpViewModel.accountNumber.observe(this, Observer { accountNumber ->
                binding.tvAccountNumber.text = accountNumber
            })
            /** observing acc balance value, cause viewmodel assigning them late */
            signUpViewModel.accountBalance.observe(this, Observer { accountBalance ->
                val rupeeSymbol = "₹ "
                var totalAmount = accountBalance
                binding.tvCurrentBalance.text = rupeeSymbol + totalAmount
            })
        }
        setSupportActionBar(binding.toolbar)
        viewPager = binding.pager

        binding.addIcon.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddTransactionActivity::class.java))
        }

        /** Attaching Viewpager2 with TabLayout*/
        val viewPagerAdapter = ViewPagerAdapter(this@MainActivity)
        viewPagerAdapter.addFragment(
            TransactionFragment(10),
            "Latest 10 Transactions"
        )
        viewPagerAdapter.addFragment(
            TransactionFragment(0),
            "Detailed Statement"
        )
        viewPagerAdapter.addFragment(
            ThirdFragment(),
            "More Option"
        )

        //viewPagerAdapter.addFragment(TransactionFragment(), "Monthly Report")

        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = viewPagerAdapter.mFragmentTitleList[position]
        }.attach()

    }

}