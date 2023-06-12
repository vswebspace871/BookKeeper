package com.example.bookkeeper.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookkeeper.R
import com.example.bookkeeper.repository.UserRepository
import kotlinx.coroutines.launch

class SignInViewModel(val userRepo: UserRepository) : ViewModel(
) {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val loginEnabled: MediatorLiveData<Boolean> = MediatorLiveData()
    val isUserLoggedIn: MutableLiveData<Boolean> = MutableLiveData()

    lateinit var context : Context

    init {
        loginEnabled.postValue(false)
        loginEnabled.addSource(username) {
            loginEnabled.value = isLoginButtonEnabled()
        }
        loginEnabled.addSource(password) {
            loginEnabled.value = isLoginButtonEnabled()
        }
    }


    fun loginUser() {
        viewModelScope.launch {
            var list = userRepo.logInUser(username.value.toString(), password.value.toString())
            if (list.isNotEmpty()) {
                isUserLoggedIn.postValue(true)
            } else {
                isUserLoggedIn.postValue(false)
            }
        }
    }

    private fun isLoginButtonEnabled(): Boolean {
        return username.value.orEmpty().isNotEmpty() && password.value.orEmpty().isNotEmpty()
    }

}