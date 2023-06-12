package com.example.bookkeeper.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookkeeper.entity.Entry

@Dao
interface TransactionDao {

    @Insert
    suspend fun createTransaction(entryModel: Entry): Long

    /*@Query("SELECT * FROM transaction")
    suspend fun getTransaction(): List<Transaction>*/

    @Query("SELECT * FROM entry WHERE user_id=:userId ORDER BY id DESC LIMIT 10")
    suspend fun getTransactions(userId : Int): List<Entry>

    @Query("SELECT * FROM entry WHERE user_id=:userId AND dateAndTime BETWEEN :from AND :to ORDER BY id DESC LIMIT 20")
    suspend fun getTransactionBetweenTwoDates(userId : Int, from : Long, to : Long) : List<Entry>

    @Query("SELECT * FROM entry WHERE user_id=:userId AND dateAndTime BETWEEN :from AND :to AND id < :id ORDER BY id DESC LIMIT 20")
    suspend fun loadNext(id: Int, userId : Int, from : Long, to : Long) : List<Entry>

    /*@Query("SELECT * FROM transaction")
    suspend fun getAllTransaction(username : String) : LiveData<List<Transaction>>*/
}