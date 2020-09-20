package com.jakode.contacts.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.jakode.contacts.data.model.UserInfo
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

object VCard {
    private lateinit var firstName: String
    private lateinit var lastName: String

    fun getVCard(context: Context, usersInfo: List<UserInfo>): ArrayList<Uri> {
        val uriList = ArrayList<Uri>()
        usersInfo.forEach {
            val (firstName, lastName) = it.user.name.split(";;")
            this.firstName = firstName
            this.lastName = lastName

            val name = "$firstName $lastName"
            val vcf = File(context.filesDir, "$name.vcf")
            // Write a vcf file
            FileOutputStream(vcf).apply {
                write(getQuery(it).toByteArray())
                close()
            }
            // Get content Uri
            uriList.add(FileProvider.getUriForFile(context,"com.jakode.contacts.FileProvider", vcf))
        }
        return uriList
    }

    private fun getQuery(userInfo: UserInfo) = "BEGIN:VCARD\n" +
            "VERSION:3.0\n" +
            "N:$lastName;$firstName;\n" +
            "FN:$firstName $lastName\n" +
            getPhones(userInfo.phones) +
            getEmails(userInfo.emails) +
            if (userInfo.profile.birthday != null) { "BDAY:${convertDate(userInfo.profile.birthday!!)}\n" } else { "" } +
            if (userInfo.profile.description != null) { "NOTE:${userInfo.profile.description}\n" } else { "" } +
            "END:VCARD"

    private fun getPhones(phones: List<String>): String {
        val builder = StringBuilder()
        phones.forEach { builder.append("TEL;TYPE=Cell:$it\n") }
        return builder.toString()
    }

    private fun getEmails(emails: List<String>): String {
        val builder = StringBuilder()
        emails.forEach { builder.append("EMAIL;TYPE=Home:$it\n") }
        return builder.toString()
    }

    private fun convertDate(data: String): String {
        val date = data.split("/").map { it.toInt() }.toMutableList()
        if (Locale.getDefault().language == "fa") {
            DateConverter().apply {
                persianToGregorian(date[0], date[1], date[2])
                date[0] = year
                date[1] = month
                date[2] = day
            }
        }
        return "${date[0]}-${date[1]}-${date[2]}"
    }
}