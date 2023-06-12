package com.example.bookkeeper.viewmodels

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookkeeper.MyApplication
import com.example.bookkeeper.R
import com.example.bookkeeper.entity.Entry
import com.example.bookkeeper.entity.User
import com.example.bookkeeper.repository.UserRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class SignUpViewModel(val userRepo: UserRepository) : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val fullName = MutableLiveData<String>()
    var userId: Int = 0
    var accountNumber = MutableLiveData<String>()
    var duplicateUsernameFound = true
    val result = MutableLiveData(false)
    val isSmallPassword: MutableLiveData<Boolean> = MutableLiveData(false)
    val registerEnabled: MediatorLiveData<Boolean> = MediatorLiveData()
    var duplicateUserName: MutableLiveData<Boolean> = MutableLiveData(false)

    /** transaction variables */
    var accountBalance = MutableLiveData<Double>()
    var paymentType = MutableLiveData<Int>()
    val amount = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val remarks = MutableLiveData<String>()
    var radioName = ""

    var tenTransactionsList = MutableLiveData<List<Entry>>()
    var transactionsListBetweenTwoDates = MutableLiveData<List<Entry>>()
    var loadNextList = MutableLiveData<List<Entry>>()

    init {
        registerEnabled.value = false
        registerEnabled.addSource(fullName) {
            registerEnabled.value = isRegisterButtonEnabled()
        }
        registerEnabled.addSource(username) {
            registerEnabled.value = isRegisterButtonEnabled()
        }
        registerEnabled.addSource(password) {
            registerEnabled.value = isRegisterButtonEnabled()
        }
        registerEnabled.addSource(duplicateUserName) {
            registerEnabled.value = isRegisterButtonEnabled()
        }
        registerEnabled.addSource(isSmallPassword) {
            registerEnabled.value = isRegisterButtonEnabled()
        }

        paymentType.postValue(R.id.credit)
    }

    fun createUser() {
        val signUpModel = User(
            null,
            fullName.value.toString(),
            username.value.toString(),
            password.value.toString(),
            accountNumber.value.toString(),
            accountBalance.value
        )
        //because of coroutine ,cant use in Main activity
        viewModelScope.launch(Dispatchers.IO) {
            val id = userRepo.createUser(signUpModel)
            if (id > -1) {
                result.postValue(true)
            } else {
                result.postValue(false)
            }
        }
    }

    fun findDuplicateUsername(username: String) {
        viewModelScope.launch {
            val userList = userRepo.getUserByUsername(username)
            if (userList != null && userList.isNotEmpty()) {
                userId = userList[0].id!!
                duplicateUserName.postValue(true)
                duplicateUsernameFound = true
            } else {
                duplicateUserName.postValue(false)
                duplicateUsernameFound = false
            }
        }
    }

    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            val listUser = userRepo.getUserByUsername(username)
            if (listUser != null && listUser.isNotEmpty()) {
                userId = listUser[0].id!!
                accountNumber.value = listUser[0].account_number
                accountBalance.value = listUser[0].account_balance!!
            }
        }
    }

    fun updateBalance(balance: Double, id: Int) {
        viewModelScope.launch {
            userRepo.updateBalance(balance, id)
        }
    }

    fun checkPasswordLength(password: String) {
        if (password.length < 4 || password.length > 4) {
            isSmallPassword.postValue(true)
        } else {
            isSmallPassword.postValue(false)
        }
    }

    private fun isRegisterButtonEnabled(): Boolean {
        return fullName.value.orEmpty().isNotEmpty() && username.value.orEmpty()
            .isNotEmpty() && password.value.orEmpty()
            .isNotEmpty() && !duplicateUserName.value!! && !isSmallPassword.value!!
    }

    /** Transaction method */

    fun createTransaction() {
        radioName = paymentType.value?.let {
            MyApplication.instance.applicationContext.resources.getResourceEntryName(
                it
            )
        }.toString()
        val entryModel = Entry(
            null,
            userId!!,
            radioName,
            amount.value.toString(),
            type.value.toString(),
            remarks.value.toString()
        )
        //because of coroutine ,cant use in Main activity
        viewModelScope.launch(Dispatchers.IO) {
            val id = userRepo.createTransaction(entryModel)
            if (id > -1) {
                result.postValue(true)
            } else {
                result.postValue(false)
            }
        }
    }

    fun getTransactions() {
        viewModelScope.launch() {
            tenTransactionsList.value = userRepo.getTransactions()
        }
    }

    fun getTransactionBetweenTwoDates(from: Long, to: Long) {
        Log.d("TAG", "getTransactionBetweenTwoDates: USERID = $userId")
        viewModelScope.launch {
            transactionsListBetweenTwoDates.value =
                userRepo.getTransactionBetweenTwoDates(userId, from, to)
        }
    }

    fun loadNext(id: Int, from: Long, to: Long) {
        viewModelScope.launch {
            loadNextList.value = userRepo.loadNext(id, userId, from, to)
        }
    }

}