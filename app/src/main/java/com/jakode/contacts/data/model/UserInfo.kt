package com.jakode.contacts.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class UserInfo(
    val user: @RawValue User,
    val profile: @RawValue Profile,
    val phones: List<String>,
    val emails: List<String>
) : Parcelable