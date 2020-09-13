package com.jakode.contacts.data.local.db.dao

import androidx.room.*
import com.jakode.contacts.data.model.User
import com.jakode.contacts.data.model.UserAndProfile
import com.jakode.contacts.data.model.UserWithEmails
import com.jakode.contacts.data.model.UserWithPhones

@Dao
interface UserDao : BaseDao<User> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(vararg entity: User)

    @Update
    override fun update(vararg entity: User)

    @Delete
    override fun delete(vararg entity: User)

    @Query("DELETE FROM users WHERE user_id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM users")
    fun deleteAll()

    @Query("SELECT * From users")
    fun getAllUsers(): List<User>

    //------------------------------------------------------------------------------------------
    @Transaction
    @Query("SELECT * FROM users")
    fun getAllUsersAndProfiles(): List<UserAndProfile>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserAndProfile(id: String): UserAndProfile

    @Transaction
    @Query("SELECT * FROM users WHERE first_name LIKE :firstName or last_name LIKE :lastName")
    fun getUsersAndProfileByName(firstName: String, lastName: String): List<UserAndProfile>

    @Transaction
    @Query("SELECT * FROM users WHERE is_block = 0")
    fun getByNotBlock(): List<UserAndProfile>

    @Transaction
    @Query("SELECT * FROM users WHERE is_block = 1")
    fun getByBlock(): List<UserAndProfile>

    @Transaction
    @Query("SELECT * FROM users WHERE is_trash = 0")
    fun getByNotTrash(): List<UserAndProfile>

    @Transaction
    @Query("SELECT * FROM users WHERE is_trash = 1")
    fun getByTrash(): List<UserAndProfile>

    //------------------------------------------------------------------------------------------
    @Transaction
    @Query("SELECT * FROM users")
    fun getAllUsersWithPhones(): List<UserWithPhones>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithPhones(id: String): UserWithPhones

    @Transaction
    @Query("SELECT * FROM users WHERE first_name LIKE :firstName or last_name LIKE :lastName")
    fun getUsersWithPhonesByName(firstName: String, lastName: String): List<UserWithPhones>

    //------------------------------------------------------------------------------------------
    @Transaction
    @Query("SELECT * FROM users")
    fun getAllUsersWithEmails(): List<UserWithEmails>

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserWithEmails(id: String): UserWithEmails

    @Transaction
    @Query("SELECT * FROM users WHERE first_name LIKE :firstName or last_name LIKE :lastName")
    fun getUsersWithEmailsByName(firstName: String, lastName: String): List<UserWithEmails>
}