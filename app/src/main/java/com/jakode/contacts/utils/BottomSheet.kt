package com.jakode.contacts.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserInfo
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.view.*
import java.lang.StringBuilder

class BottomSheet(context: Context, theme: Int, userInfo: UserInfo) :
    BottomSheetDialog(context, theme) {
    private var view: View =
        LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet, bottomSheetContainer)

    init {
        setContentView(view)
        onClick(userInfo)
    }

    private fun onClick(userInfo: UserInfo) {
        // Share with file
        view.file.setOnClickListener {
            dismiss()
            Intents.sendVCard(context, userInfo)
        }

        // Share with text
        view.text.setOnClickListener {
            dismiss()
            Intents.sendText(context, getUserText(userInfo))
        }
    }

    private fun getUserText(userInfo: UserInfo): String {
        return "[${getName()}] ${userInfo.user.name.firstName} ${userInfo.user.name.lastName}\n" +
                getPhones(userInfo.phones) +
                getEmails(userInfo.emails) +
                if (userInfo.profile.birthday != null) { "[${getBirthday()}] ${userInfo.profile.birthday}\n" } else { "" } +
                if (userInfo.profile.address != null) { "[${getAddress()}] ${userInfo.profile.address}\n" } else { "" } +
                if (userInfo.profile.description != null) { "[${getDescription()}] ${userInfo.profile.description}\n" } else { "" }
    }

    private fun getPhones(phones: List<String>): String {
        val builder = StringBuilder()
        phones.forEach { builder.append("[${getPhone()}] $it\n") }
        return builder.toString()
    }

    private fun getEmails(emails: List<String>): String {
        val builder = StringBuilder()
        emails.forEach { builder.append("[${getEmail()}] $it\n") }
        return builder.toString()
    }

    @SuppressLint("ResourceType")
    private fun getName() = context.resources.getString(R.string.name)
    private fun getPhone() = context.resources.getString(R.string.phone)
    private fun getEmail() = context.resources.getString(R.string.email)
    private fun getBirthday() = context.resources.getString(R.string.birthday)
    private fun getAddress() = context.resources.getString(R.string.address)
    private fun getDescription() = context.resources.getString(R.string.description)
}