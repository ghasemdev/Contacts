package com.jakode.contacts.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithEmails(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_creator_id"
    )
    val emails: List<Email>
)