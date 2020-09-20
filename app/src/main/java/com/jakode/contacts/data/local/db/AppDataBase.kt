package com.jakode.contacts.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jakode.contacts.data.local.db.dao.*
import com.jakode.contacts.data.model.*
import com.jakode.contacts.utils.Converters

@Database(
    entities = [User::class, Profile::class, Phone::class, Email::class, Search::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao // Accesses to user
    abstract fun profileDao(): ProfileDao // Accesses to profile
    abstract fun phoneDao(): PhoneDao // Accesses to phones
    abstract fun emailDao(): EmailDao // Accesses to emails
    abstract fun searchDao(): SearchDao // // Accesses to search history

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        private const val DATABASE_NAME = "CONTACTS"

        fun getInstance(context: Context): AppDataBase {
            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, AppDataBase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                return INSTANCE!!
            }
        }
    }
}