package com.jakode.contacts.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class Email(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "email_id") val id: Long,
    @ColumnInfo(name = "user_creator_id") val userId: Long,
    val email: String
) {
    constructor(userId: Long, email: String) :
            this(0, userId, email)
}