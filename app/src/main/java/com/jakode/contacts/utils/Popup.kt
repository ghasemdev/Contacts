package com.jakode.contacts.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.jakode.contacts.R
import kotlinx.android.synthetic.main.popup_layout.view.*

object PopupMenu {
    // PopupWindow display method
    @SuppressLint("InflateParams")
    fun show(view: View, x: Int, y: Int) {
        // Create a View object yourself through inflater
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_layout, null)

        // Specify the length and width through constants
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        // Make Inactive Items Outside Of PopupWindow
        val focusable = true

        // Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.elevation = 5F

        // Set the location of the window on the screen
        popupWindow.showAsDropDown(view, x, y)

        // Initialize the elements of our window, install the handler
        clickListener(popupView, popupWindow, view)
    }

    private fun clickListener(
        popupView: View,
        popupWindow: PopupWindow,
        view: View
    ) {
        popupView.delete.setOnClickListener {
            popupWindow.dismiss()
            Toast.makeText(view.context, "delete", Toast.LENGTH_SHORT).show()
        }

        popupView.share.setOnClickListener {
            popupWindow.dismiss()
            Toast.makeText(view.context, "share", Toast.LENGTH_SHORT).show()
        }
    }
}