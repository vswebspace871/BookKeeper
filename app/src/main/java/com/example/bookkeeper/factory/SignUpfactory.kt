package com.example.bookkeeper.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookkeeper.repository.UserRepository
import com.example.bookkeeper.viewmodels.SignUpViewModel

class SignUpFactory(val repo : UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpViewModel(repo) as T
    }
}