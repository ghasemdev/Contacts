package com.jakode.contacts.data.model

import androidx.room.ColumnInfo

data class Name(
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String
)