package com.jakode.contacts.data.repository

import android.content.Context
import com.jakode.contacts.data.local.db.AppDataBase
import com.jakode.contacts.data.local.db.dao.EmailDao
import com.jakode.contacts.data.local.db.dao.PhoneDao
import com.jakode.contacts.data.local.db.dao.ProfileDao
import com.jakode.contacts.data.local.db.dao.UserDao
import com.jakode.contacts.data.model.*

class AppRepository(context: Context) {
    private var userDao: UserDao
    private var profileDao: ProfileDao
    private var phoneDao: PhoneDao
    private var emailDao: EmailDao

    init {
        val db = AppDataBase.getInstance(context)
        userDao = db.userDao()
        profileDao = db.profileDao()
        phoneDao = db.phoneDao()
        emailDao = db.emailDao()
    }

    fun insertUser(user: UserInfo): Long {
        userDao.insert(user.user)
        profileDao.insert(user.profile)

        val userId = getUserId()
        val phonesList = stringToPhone(user.phones, userId)
        val emailsList = stringToEmail(user.emails, userId)

        phonesList.forEach { phoneDao.insert(it) }
        emailsList.forEach { emailDao.insert(it) }
        return userId
    }

    fun updateUser(user: UserAndProfile, phones: List<Phone>, emails: List<Email>) {
        userDao.update(user.user)
        profileDao.update(user.profile)
        phones.forEach { phoneDao.update(it) }
        emails.forEach { emailDao.update(it) }
    }

    fun deleteUser(user: UserAndProfile, phones: List<Phone>, emails: List<Email>) {
        userDao.delete(user.user)
        profileDao.delete(user.profile)
        phones.forEach { phoneDao.delete(it) }
        emails.forEach { emailDao.delete(it) }
    }

    fun deleteUser(id: String) {
        userDao.deleteById(id)
        profileDao.deleteById(id)
        phoneDao.deleteByUserId(id)
        emailDao.deleteByUserId(id)
    }

    fun deleteAllUsers() {
        userDao.deleteAll()
        profileDao.deleteAll()
        phoneDao.deleteAll()
        emailDao.deleteAll()
    }

    fun getAllUsers(): ArrayList<UserInfo> {
        val list = ArrayList<UserInfo>()

        val users = getAllUsersAndProfiles()
        val phones = getAllUsersWithPhones()
        val emails = getAllUsersWithEmails()

        var index = 0
        users.forEach {
            list.add(
                UserInfo(
                    it.user,
                    it.profile,
                    phoneToString(phones[index].phones),
                    emailToString(emails[index].emails)
                )
            )
            index++
        }
        list.sortBy { it.user.name.firstName }
        return list
    }

    private fun getAllUsersAndProfiles() = userDao.getAllUsersAndProfiles()
    private fun getAllUsersWithPhones() = userDao.getAllUsersWithPhones()
    private fun getAllUsersWithEmails() = userDao.getAllUsersWithEmails()

    fun findUserById(id: String) = userDao.getUserAndProfile(id)
    fun findUserWithPhonesById(id: String) = userDao.getUserWithPhones(id)
    fun findUserWithEmailsById(id: String) = userDao.getUserWithEmails(id)

    fun findUsersByName(name: Name): List<UserAndProfile> {
        val firstName = "${name.firstName}%"
        val lastName = "%${name.lastName}"
        return userDao.getUsersAndProfileByName(firstName, lastName)
    }

    fun findUsersWithPhonesByName(name: Name): List<UserWithPhones> {
        val firstName = "${name.firstName}%"
        val lastName = "%${name.lastName}"
        return userDao.getUsersWithPhonesByName(firstName, lastName)
    }

    fun findUsersWithEmailsByName(name: Name): List<UserWithEmails> {
        val firstName = "${name.firstName}%"
        val lastName = "%${name.lastName}"
        return userDao.getUsersWithEmailsByName(firstName, lastName)
    }

    fun findUserByPhone(number: String): List<UserAndProfile> {
        val list = ArrayList<UserAndProfile>()
        for (phone in phoneDao.getByPhone(number))
            list.add(findUserById(phone.userId.toString()))
        return list
    }

    fun findUsersByBlock(boolean: Boolean): List<UserAndProfile> {
        return when (boolean) {
            true -> userDao.getByBlock()
            false -> userDao.getByNotBlock()
        }
    }

    fun findUsersByTrash(boolean: Boolean): List<UserAndProfile> {
        return when (boolean) {
            true -> userDao.getByTrash()
            false -> userDao.getByNotTrash()
        }
    }

    private fun stringToPhone(phones: List<String>, userId: Long): List<Phone> {
        val phonesList = ArrayList<Phone>()
        for (phone in phones) phonesList.add(Phone(userId, phone))
        return phonesList
    }

    private fun stringToEmail(emails: List<String>, userId: Long): List<Email> {
        val emailsList = ArrayList<Email>()
        for (email in emails) emailsList.add(Email(userId, email))
        return emailsList
    }

    private fun phoneToString(phones: List<Phone>): List<String> {
        val phonesList = ArrayList<String>()
        for (phone in phones) phonesList.add(phone.number)
        return phonesList
    }

    private fun emailToString(emails: List<Email>): List<String> {
        val emailsList = ArrayList<String>()
        for (email in emails) emailsList.add(email.email)
        return emailsList
    }

    private fun getUserId(): Long {
        val users = userDao.getAllUsers()
        return users[users.size - 1].id
    }
}