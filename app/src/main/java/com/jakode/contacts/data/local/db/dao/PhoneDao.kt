package com.jakode.contacts.data.local.db.dao

import androidx.room.*
import com.jakode.contacts.data.model.Phone

@Dao
interface PhoneDao : BaseDao<Phone> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(vararg entity: Phone)

    @Update
    override fun update(vararg entity: Phone)

    @Delete
    override fun delete(vararg entity: Phone)

    @Query("DELETE FROM phones WHERE phone_id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM phones WHERE user_creator_id = :userId")
    fun deleteByUserId(userId: String)

    @Query("DELETE FROM phones")
    fun deleteAll()

    @Query("SELECT * FROM phones WHERE number = :phone")
    fun getByPhone(phone: String): List<Phone>
}