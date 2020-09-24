package com.jakode.contacts.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jakode.contacts.data.local.db.dao.*
import com.jakode.contacts.data.model.*
import com.jakode.contacts.utils.Converters

@Database(
    entities = [User::class, Profile::class, Phone::class, Email::class, Search::class, Recent::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao // Accesses to user
    abstract fun profileDao(): ProfileDao // Accesses to profile
    abstract fun phoneDao(): PhoneDao // Accesses to phones
    abstract fun emailDao(): EmailDao // Accesses to emails
    abstract fun searchDao(): SearchDao // Accesses to search history
    abstract fun recentDao(): RecentDao // Accesses to Recent

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        private const val DATABASE_NAME = "CONTACTS"

        private const val USER_TABLE = "users"
        private const val USER_ID = "user_id"

        private const val RECENT_TABLE = "recent"
        private const val RECENT_ID = "id"
        private const val RECENT_USER_ID = "user_id"
        private const val RECENT_UPDATE_IN = "updated_in"

        // Migration of version 1 -> 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS $RECENT_TABLE (" +
                            "$RECENT_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "$RECENT_USER_ID INTEGER NOT NULL," +
                            "$RECENT_UPDATE_IN INTEGER," +
                            "FOREIGN KEY ($RECENT_USER_ID) REFERENCES $USER_TABLE($USER_ID)" +
                            "on delete CASCADE on update CASCADE" +
                            ");"
                )
            }
        }

        fun getInstance(context: Context): AppDataBase {
            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, AppDataBase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .build()
                return INSTANCE!!
            }
        }
    }
}