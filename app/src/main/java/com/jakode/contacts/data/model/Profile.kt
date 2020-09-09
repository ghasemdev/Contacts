package com.jakode.contacts.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "profile_id") val id: Long,
    val cover: String,
    val birthday: String?,
    val address: String?,
    val description: String?
) {
    constructor(cover: String, birthday: String?, address: String?, description: String?) :
            this(0, cover, birthday, address, description)
}