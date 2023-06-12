package com.example.bookkeeper.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user", indices = [Index(value = ["userName"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var fullName: String,
    var userName: String,
    var password: String,
    var account_number: String,
    var account_balance: Double?
)


