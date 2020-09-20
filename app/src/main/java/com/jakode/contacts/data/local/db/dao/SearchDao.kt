package com.jakode.contacts.data.local.db.dao

import androidx.room.*
import com.jakode.contacts.data.model.Search

@Dao
interface SearchDao : BaseDao<Search> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(vararg entity: Search)

    @Update
    override fun update(vararg entity: Search)

    @Delete
    override fun delete(vararg entity: Search)

    @Query("DELETE FROM search_history")
    fun deleteAll()

    @Query("SELECT * From search_history")
    fun getAllSearch(): List<Search>

    @Query("SELECT * From search_history WHERE `query` = :query")
    fun getByQuery(query: String): Search?
}