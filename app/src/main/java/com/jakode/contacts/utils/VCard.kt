package com.jakode.contacts.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.jakode.contacts.data.model.UserInfo
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.util.*

object VCard {
    fun getVCard(context: Context, userInfo: UserInfo): Uri {
        // Write a vcf file
        val vcf = File(context.filesDir, "contact.vcf")
        FileOutputStream(vcf).apply {
            write(getQuery(userInfo).toByteArray())
            close()
        }

        // Get content Uri
        return FileProvider.getUriForFile(context, "com.jakode.contacts.FileProvider", vcf)
    }

    private fun getQuery(userInfo: UserInfo) = "BEGIN:VCARD\n" +
            "VERSION:4.0\n" +
            "N:${userInfo.user.name.lastName};${userInfo.user.name.firstName}\n" +
            "FN:${userInfo.user.name.firstName} ${userInfo.user.name.lastName}\n" +
            getPhones(userInfo.phones) +
            getEmails(userInfo.emails) +
            if (userInfo.profile.birthday != null) { "BDAY:${convertDate(userInfo.profile.birthday)}\n" } else { "" } +
            if (userInfo.profile.description != null) { "NOTE:${userInfo.profile.description}\n" } else { "" } +
            "x-qq:21588891\n" +
            "END:VCARD"

    private fun getPhones(phones: List<String>): String {
        val builder = StringBuilder()
        phones.forEach { builder.append("TEL;TYPE=mobile,voice;Phone:$it\n") }
        return builder.toString()
    }

    private fun getEmails(emails: List<String>): String {
        val builder = StringBuilder()
        emails.forEach { builder.append("EMAIL:$it\n") }
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

        return Calendar.getInstance().run {
            set(get(Calendar.YEAR), date[1] - 1, date[2], 0, 0, 0)
            this.timeInMillis
        }.toString()
    }
}