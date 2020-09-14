package com.jakode.contacts.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.data.repository.AppRepository
import kotlinx.android.synthetic.main.bottom_delete_layout.view.*
import kotlinx.android.synthetic.main.bottom_share_layout.*
import kotlinx.android.synthetic.main.bottom_share_layout.view.*

class BottomSheet(type: Type, activity: Activity, theme: Int, userInfo: UserInfo) :
    BottomSheetDialog(activity, theme) {
    private var navController: NavigateManager = activity as NavigateManager
    private var appRepository: AppRepository = AppRepository(context)

    private var view: View = when (type) {
        Type.BOTTOM_SHARE -> LayoutInflater.from(context)
            .inflate(R.layout.bottom_share_layout, bottomSheetContainer)
        Type.BOTTOM_DELETE -> LayoutInflater.from(context)
            .inflate(R.layout.bottom_delete_layout, bottomSheetContainer)
    }

    enum class Type {
        BOTTOM_SHARE, BOTTOM_DELETE
    }

    init {
        setContentView(view)
        onClick(type, userInfo)
    }

    private fun onClick(type: Type, userInfo: UserInfo) {
        when (type) {
            Type.BOTTOM_SHARE -> {
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
            Type.BOTTOM_DELETE -> {
                // Cancel
                view.cancel.setOnClickListener {
                    dismiss()
                }

                // Delete
                view.move.setOnClickListener {
                    dismiss()
                    appRepository.deleteUser(userInfo.user.id.toString())
                    navController.navigateUp()
                }
            }
        }
    }

    private fun getUserText(userInfo: UserInfo): String {
        return "[${getName()}] ${userInfo.user.name.firstName} ${userInfo.user.name.lastName}\n" +
                getPhones(userInfo.phones) +
                getEmails(userInfo.emails) +
                if (userInfo.profile.birthday != null) {
                    "[${getBirthday()}] ${userInfo.profile.birthday}\n"
                } else {
                    ""
                } +
                if (userInfo.profile.address != null) {
                    "[${getAddress()}] ${userInfo.profile.address}\n"
                } else {
                    ""
                } +
                if (userInfo.profile.description != null) {
                    "[${getDescription()}] ${userInfo.profile.description}\n"
                } else {
                    ""
                }
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