package com.jakode.contacts.data.local.db.dao

import androidx.room.*
import com.jakode.contacts.data.model.Profile

@Dao
interface ProfileDao : BaseDao<Profile> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(vararg entity: Profile)

    @Update
    override fun update(vararg entity: Profile)

    @Delete
    override fun delete(vararg entity: Profile)

    @Query("DELETE FROM profile WHERE profile_id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM profile")
    fun deleteAll()
}