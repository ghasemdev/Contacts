package com.jakode.contacts.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id") val id: Long,
    @Embedded val name: Name,
    @ColumnInfo(name = "is_block") val isBlock: Boolean,
    @ColumnInfo(name = "is_trash") val isTrash: Boolean
) {
    constructor(name: Name, isBlock: Boolean, isTrash: Boolean) :
            this(0, name, isBlock, isTrash)
}