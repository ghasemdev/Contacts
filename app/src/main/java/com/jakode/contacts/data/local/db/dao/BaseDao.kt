package com.jakode.contacts.data.local.db.dao

interface BaseDao<T> {
    fun insert(vararg entity: T)                              // Create object
    fun update(vararg entity: T)                              // Edit object
    fun delete(vararg entity: T)                              // Delete object
}