package com.jakode.contacts.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView

class ButtonBox(
    private val deleteBtn: Button,
    private val deleteIc: ImageView,
    private val shareBtn: Button,
    private val shareIc: ImageView,
) {
    fun hideDeleteButton() {
        deleteBtn.visibility = View.GONE
        deleteIc.visibility = View.GONE
    }

    fun hideShareButton() {
        shareBtn.visibility = View.GONE
        shareIc.visibility = View.GONE
    }

    fun showDeleteButton() {
        deleteBtn.visibility = View.VISIBLE
        deleteIc.visibility = View.VISIBLE
    }

    fun showShareButton() {
        shareBtn.visibility = View.VISIBLE
        shareIc.visibility = View.VISIBLE
    }

    fun showButtons() {
        showDeleteButton()
        showShareButton()
    }
}