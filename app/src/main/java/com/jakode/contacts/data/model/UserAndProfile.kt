package com.jakode.contacts.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndProfile(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "profile_id"
    )
    val profile: Profile
)