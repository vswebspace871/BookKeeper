package com.example.bookkeeper

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.example.bookkeeper.database.UserDatabase
import com.example.bookkeeper.repository.UserRepository

class MyApplication: Application() {

    init {
        instance = this
    }


    companion object {
        //private lateinit var appContext: Context
        lateinit var database:UserDatabase
        lateinit var instance: MyApplication


        fun getUserRepository(context: Context?): UserRepository {
            database = UserDatabase.getDatabase(context!!.applicationContext)
            //initialise notes repository
            return UserRepository(database)
        }
    }

    override fun onCreate() {
        super.onCreate()

        //instance = this
    }
}