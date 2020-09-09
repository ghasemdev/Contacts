package com.jakode.contacts.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phones")
data class Phone(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "phone_id") val id: Long,
    @ColumnInfo(name = "user_creator_id") val userId: Long,
    val number: String
) {
    constructor(userId: Long, number: String) :
            this(0, userId, number)
}