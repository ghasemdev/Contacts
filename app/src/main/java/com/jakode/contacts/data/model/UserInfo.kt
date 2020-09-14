package com.jakode.contacts.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class UserInfo(
    val user: @RawValue User,
    val profile: @RawValue Profile,
    var phones: List<String>,
    var emails: List<String>,
    var isVisible: Boolean = false,
    var isSelected: Boolean = false
) : Parcelable