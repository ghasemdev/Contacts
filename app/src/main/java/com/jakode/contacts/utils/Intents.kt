package com.jakode.contacts.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import com.jakode.contacts.data.model.UserInfo

object Intents {
    // Add event to calender
    fun addEvent(
        context: Context,
        title: String,
        description: String,
        location: String,
        startMillis: Long,
        endMillis: Long
    ) {
        Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            putExtra(CalendarContract.Events.ALL_DAY, true)
            putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY")
            putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
            putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        }.also { context.startActivity(it) }
    }

    // Show event from calender
    fun showEvent(context: Context, time: Long) {
        // URI construction
        val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
        ContentUris.appendId(builder, time)
        val uri = builder.build()

        // Set Intent action to Intent.ACTION_VIEW
        Intent(Intent.ACTION_VIEW).apply { data = uri }.also { context.startActivity(it) }
    }

    // Search in map
    fun showMap(context: Context, address: String) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=$address")
        }.also { context.startActivity(it) }
    }

    // Dial phone
    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }.also { context.startActivity(it) }
    }

    // Send message
    fun composeSmsMessage(context: Context, phoneNumber: String) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")  // This ensures only SMS apps respond
            putExtra("sms_body", "")
        }.also { context.startActivity(it) }
    }

    // dial video call
    fun dialGoogleDuo(context: Context, phoneNumber: String) {
        try {
            Intent("com.google.android.apps.tachyon.action.CALL").apply {
                // phone is the phone number your to a function
                data = Uri.parse("tel: $phoneNumber")
                setPackage("com.google.android.apps.tachyon")
            }.also { context.startActivity(it) }
        } catch (e: Exception) {
            openLink(
                context,
                "https://play.google.com/store/apps/details?id=com.google.android.apps.tachyon&hl=fa"
            )
        }
    }

    // Send email
    fun composeEmail(context: Context, to: Array<String>, subject: String, text: String) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }.also { context.startActivity(it) }
    }

    // Open Link in browser
    fun openLink(context: Context, uri: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(uri)).also {
            context.startActivity(it)
        }
    }

    fun sendVCard(context: Context, userInfo: UserInfo) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/x-vcard"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, VCard.getVCard(context, userInfo))
        }.also { context.startActivity(Intent.createChooser(it, null)) }
    }

    fun sendText(context: Context, text: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }.also { context.startActivity(Intent.createChooser(it, null)) }
    }
}