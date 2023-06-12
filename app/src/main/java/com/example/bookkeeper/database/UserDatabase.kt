package com.example.bookkeeper.database

import android.content.Context
import androidx.room.*
import com.example.bookkeeper.TimestampConverter
import com.example.bookkeeper.dao.TransactionDao
import com.example.bookkeeper.dao.UsersDao
import com.example.bookkeeper.entity.Entry
import com.example.bookkeeper.entity.User

@Database(
    entities = [
        User::class,
        Entry::class
    ], version = 1
)
@TypeConverters(TimestampConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UsersDao
    abstract fun transactionDao(): TransactionDao

    //database singleton pattern
    companion object {
        @Volatile  //for update realtime
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {

            synchronized(this) {
                // synchronised used for ek hi bana rahe instance

                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, UserDatabase::class.java, "UserDB")
                        .fallbackToDestructiveMigration()
                        .build()
                    //instance of DB creation
                }
            }
            return INSTANCE!!
        }
    }

}