package com.jakode.contacts.data.local.db.dao

import androidx.room.*
import com.jakode.contacts.data.model.Recent

@Dao
interface RecentDao : BaseDao<Recent> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(vararg entity: Recent)

    @Update
    override fun update(vararg entity: Recent)

    @Delete
    override fun delete(vararg entity: Recent)

    @Query("DELETE FROM recent")
    fun deleteAll()

    @Query("SELECT * From recent")
    fun getAllRecent(): List<Recent>

    @Query("SELECT * From recent WHERE user_id = :userId")
    fun getByUser(userId: String): Recent?
}