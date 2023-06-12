package com.example.bookkeeper.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookkeeper.entity.User

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(signUpModel: User): Long

    @Query("SELECT * FROM user")
    suspend fun getUser(): List<User>

    @Query("SELECT * FROM user WHERE userName=:username")
    suspend fun getUserByUsername(username: String): List<User>

    @Query("SELECT * FROM user WHERE userName=:username AND password=:password")
    suspend fun logInUser(username: String, password: String): List<User>

    @Query(
        "UPDATE user SET account_balance=:balance WHERE id=:id"
    )
    suspend fun updateBalance(balance: Double, id: Int)

}