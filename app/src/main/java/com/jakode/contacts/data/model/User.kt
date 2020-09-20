package com.jakode.contacts.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id") var id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_block") val isBlock: Boolean,
    @ColumnInfo(name = "is_trash") val isTrash: Boolean
) : Parcelable {
    constructor(name: String, isBlock: Boolean, isTrash: Boolean) :
            this(0, name, isBlock, isTrash)
}