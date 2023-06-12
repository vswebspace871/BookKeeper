package com.example.bookkeeper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(
    tableName = "entry"
)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var user_id: Int,
    var payment_type: String,
    var amount: String,
    var type: String,
    var remarks: String,
    var dateAndTime: Date? = Date()
    /** Save date in database and use TypeConverter */
)