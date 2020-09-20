package com.jakode.contacts.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "search_history")
data class Search(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var query: String,
    var date: Date?
) {
    constructor(query: String, date: Date?) : this(0, query, date)
}