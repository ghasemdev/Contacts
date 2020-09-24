package com.jakode.contacts.data.repository

import android.content.Context
import com.jakode.contacts.data.local.db.AppDataBase
import com.jakode.contacts.data.local.db.dao.*
import com.jakode.contacts.data.model.*

class AppRepository(context: Context) {
    private var userDao: UserDao
    private var profileDao: ProfileDao
    private var phoneDao: PhoneDao
    private var emailDao: EmailDao
    private var searchDao: SearchDao
    private var recentDao: RecentDao

    init {
        val db = AppDataBase.getInstance(context)
        userDao = db.userDao()
        profileDao = db.profileDao()
        phoneDao = db.phoneDao()
        emailDao = db.emailDao()
        searchDao = db.searchDao()
        recentDao = db.recentDao()
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

    fun updateUser(user: UserInfo) {
        userDao.update(user.user)
        profileDao.update(user.profile)

        val userId = user.user.id
        val phonesList = stringToPhoneUpdate(user.phones, userId)
        val emailsList = stringToEmailUpdate(user.emails, userId)

        phonesList.forEach { phoneDao.update(it) }
        emailsList.forEach { emailDao.update(it) }
    }

    fun deleteUser(id: String) {
        userDao.deleteById(id)
        profileDao.deleteById(id)
        phoneDao.deleteByUserId(id)
        emailDao.deleteByUserId(id)
    }

    fun deleteUsers(ids: List<String>) {
        ids.forEach { deleteUser(it) }
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
        list.sortBy { it.user.name }
        return list
    }

    private fun getAllUsersAndProfiles() = userDao.getAllUsersAndProfiles()
    private fun getAllUsersWithPhones() = userDao.getAllUsersWithPhones()
    private fun getAllUsersWithEmails() = userDao.getAllUsersWithEmails()

    fun findUsers(query: String): ArrayList<UserInfo> {
        return when {
            isNumber(query) -> findUserByPhone(query)
            else -> findUsersByName(query)
        }
    }

    fun findUsersByName(query: String): ArrayList<UserInfo> {
        val list = ArrayList<UserInfo>()

        var index = 0
        findUsersAndProfiles(query).forEach {
            list.add(
                UserInfo(
                    it.user,
                    it.profile,
                    phoneToString(findUsersWithPhones(query)[index].phones),
                    emailToString(findUsersWithEmails(query)[index].emails)
                )
            )
            index++
        }
        list.sortBy { it.user.name }
        return list
    }

    private fun findUsersAndProfiles(query: String): List<UserAndProfile> {
        val name = "%${query.replaceFirst(" ", ";;")}%"
        return userDao.getUsersAndProfileByName(name)
    }

    private fun findUsersWithPhones(query: String): List<UserWithPhones> {
        val name = "%${query.replaceFirst(" ", ";;")}%"
        return userDao.getUsersWithPhonesByName(name)
    }

    private fun findUsersWithEmails(query: String): List<UserWithEmails> {
        val name = "%${query.replaceFirst(" ", ";;")}%"
        return userDao.getUsersWithEmailsByName(name)
    }

    fun findUserByPhone(query: String): ArrayList<UserInfo> {
        fun getPhones(userId: String, phone: Phone): ArrayList<Phone> {
            val phones = findUserWithPhonesById(userId).phones as ArrayList
            phones.remove(phone)
            phones.add(0, phone)
            return phones
        }

        val list = ArrayList<UserInfo>()

        val number = "%$query%"
        for (phone in phoneDao.getByPhone(number)) {
            val userId = phone.userId.toString()
            val userAndProfile = findUserById(userId)

            list.add(
                UserInfo(
                    userAndProfile.user,
                    userAndProfile.profile,
                    phoneToString(getPhones(userId, phone)),
                    emailToString(findUserWithEmailsById(userId).emails)
                )
            )
        }
        list.sortBy { it.user.name }
        return list
    }

    private fun findUserById(id: String) = userDao.getUserAndProfile(id)
    private fun findUserWithPhonesById(id: String) = userDao.getUserWithPhones(id)
    private fun findUserWithEmailsById(id: String) = userDao.getUserWithEmails(id)

    fun insertSearch(search: Search) {
        searchDao.getByQuery(search.query)?.let {
            deleteSearch(it)
        }
        searchDao.insert(search)
    }

    fun deleteSearch(search: Search) = searchDao.delete(search)
    fun deleteAllSearch() = searchDao.deleteAll()
    fun getAllSearch() = searchDao.getAllSearch().reversed()

    fun insertRecent(recent: Recent) {
        recentDao.getByUser(recent.userId.toString())?.let {
            deleteRecent(it)
        }
        recentDao.insert(recent)
    }

    fun deleteRecent(recent: Recent) = recentDao.delete(recent)
    fun deleteAllRecent() = recentDao.deleteAll()
    fun getAllRecent() = recentDao.getAllRecent().reversed()

    fun findUser(id: String): UserInfo {
        val userAndProfile = findUserById(id)
        val phones = findUserWithPhonesById(id).phones
        val emails = findUserWithEmailsById(id).emails
        return UserInfo(
            userAndProfile.user,
            userAndProfile.profile,
            phones.map { it.number },
            emails.map { it.email })
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

    private fun stringToPhoneUpdate(phones: List<String>, userId: Long): List<Phone> {
        val phonesList = ArrayList<Phone>()
        val userPhones = userDao.getUserWithPhones(userId.toString()).phones

        when (userPhones.size == phones.size) {
            true -> { // Maybe something change
                for (index in phones.indices) {
                    phonesList.add(Phone(userPhones[index].id, userId, phones[index]))
                }
            }
            else -> { // Add or Remove number
                // Step 1 : remove all numbers belong user
                phoneDao.deleteByUserId(userId.toString())
                // Step 2 : add new numbers
                stringToPhone(phones, userId).forEach { phoneDao.insert(it) }
            }
        }
        return phonesList
    }

    private fun stringToEmailUpdate(emails: List<String>, userId: Long): List<Email> {
        val emailsList = ArrayList<Email>()
        val userEmails = userDao.getUserWithEmails(userId.toString()).emails

        when (userEmails.size == emails.size) {
            true -> { // Maybe something change
                for (index in emails.indices) {
                    emailsList.add(Email(userEmails[index].id, userId, emails[index]))
                }
            }
            else -> { // Add or Remove number
                // Step 1 : remove all emails belong user
                emailDao.deleteByUserId(userId.toString())
                // Step 2 : add new emails
                stringToEmail(emails, userId).forEach { emailDao.insert(it) }
            }
        }
        return emailsList
    }

    private fun getUserId(): Long {
        val users = userDao.getAllUsers()
        return users[users.size - 1].id
    }

    private fun isNumber(query: String): Boolean {
        var state = true
        for (char in query) {
            val ascii = char.toInt()
            if (ascii < 48 || ascii > 57) {
                state = false
                break
            }
        }
        return state
    }
}