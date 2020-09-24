package com.jakode.contacts.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "recent",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("user_id"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Recent(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "user_id") var userId: Long,
    @ColumnInfo(name = "updated_in") var date: Date?
) {
    constructor(userId: Long, date: Date?) : this(0, userId, date)
}