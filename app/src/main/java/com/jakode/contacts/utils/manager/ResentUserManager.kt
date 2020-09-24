package com.jakode.contacts.utils.manager

interface ResentUserManager {
    var pos: Int
    fun remove(position: Int)
    fun removeAll()
}