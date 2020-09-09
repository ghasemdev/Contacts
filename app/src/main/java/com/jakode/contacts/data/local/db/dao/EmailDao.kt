package com.jakode.contacts.data.local.db.dao

import androidx.room.*
import com.jakode.contacts.data.model.Email

@Dao
interface EmailDao : BaseDao<Email> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(vararg entity: Email)

    @Update
    override fun update(vararg entity: Email)

    @Delete
    override fun delete(vararg entity: Email)

    @Query("DELETE FROM emails WHERE email_id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM emails WHERE user_creator_id = :userId")
    fun deleteByUserId(userId: String)

    @Query("DELETE FROM emails")
    fun deleteAll()
}