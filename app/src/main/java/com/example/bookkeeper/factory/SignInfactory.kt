package com.example.bookkeeper.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookkeeper.repository.UserRepository
import com.example.bookkeeper.viewmodels.SignInViewModel
import com.example.bookkeeper.viewmodels.SignUpViewModel

class SignInFactory(val repo : UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(repo) as T
    }
}