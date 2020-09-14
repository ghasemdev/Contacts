package com.jakode.contacts.utils

interface SelectionManager {
    var selectionMode: Boolean

    fun onContactAction(isSelected: Boolean) {
        selectionMode = isSelected
    }
}