package com.jakode.contacts.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jakode.contacts.data.local.db.dao.EmailDao
import com.jakode.contacts.data.local.db.dao.PhoneDao
import com.jakode.contacts.data.local.db.dao.ProfileDao
import com.jakode.contacts.data.local.db.dao.UserDao
import com.jakode.contacts.data.model.Email
import com.jakode.contacts.data.model.Phone
import com.jakode.contacts.data.model.Profile
import com.jakode.contacts.data.model.User

@Database(entities = [User::class, Profile::class, Phone::class, Email::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao // Accesses to user
    abstract fun profileDao(): ProfileDao // Accesses to profile
    abstract fun phoneDao(): PhoneDao // Accesses to phones
    abstract fun emailDao(): EmailDao // Accesses to emails

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