package com.example.bookkeeper.repository

import com.example.bookkeeper.database.UserDatabase
import com.example.bookkeeper.entity.Entry
import com.example.bookkeeper.entity.User

class UserRepository(private val database : UserDatabase) {

    /** creating User Method */

    suspend fun createUser(signupModel : User): Long{
        //run background thread
        return database.userDao().createUser(signupModel)
    }

    suspend fun getUser() : List<User> {
        return database.userDao().getUser()
    }

    suspend fun getUserByUsername(username : String): List<User>{
        return database.userDao().getUserByUsername(username)
    }

    suspend fun logInUser(username : String, password : String): List<User> {
        return database.userDao().logInUser(username, password)
    }

    suspend fun updateBalance(balance: Double, id: Int){
        return database.userDao().updateBalance(balance, id)
    }

    /** transaction Method */

    suspend fun createTransaction(entryModel : Entry) : Long {
        return database.transactionDao().createTransaction(entryModel)
    }

    suspend fun getTransactions(): List<Entry> {
        return database.transactionDao().getTransactions(1)
    }

   suspend fun getTransactionBetweenTwoDates(userId : Int, from : Long, to : Long) : List<Entry> {
        return database.transactionDao().getTransactionBetweenTwoDates(userId, from, to)
    }

    suspend fun loadNext(id: Int, userId : Int, from : Long, to : Long) : List<Entry> {
        return database.transactionDao().loadNext(id, userId, from, to)
    }

    /*suspend fun getAllTransaction(username : String) : LiveData<List<Transaction>> {
        return database.transactionDao().getAllTransaction(username)
    }*/

}