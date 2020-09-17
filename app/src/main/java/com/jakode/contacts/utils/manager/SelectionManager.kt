package com.jakode.contacts.utils.manager

import com.jakode.contacts.data.model.UserInfo

interface SelectionManager {
    var selectionMode: Boolean

    fun onContactAction(isSelected: Boolean) {
        selectionMode = isSelected
    }

    fun removeUsers(selectedUser: List<UserInfo>)
}