package com.jakode.contacts.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "profile_id") var id: Long,
    var cover: String,
    var birthday: String?,
    var address: String?,
    var description: String?
) : Parcelable {
    constructor(cover: String, birthday: String?, address: String?, description: String?) :
            this(0, cover, birthday, address, description)
}