package com.jakode.contacts.utils.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.jakode.contacts.R
import com.jakode.contacts.data.model.Recent
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.utils.ButtonBox
import com.jakode.contacts.utils.manager.ResentUserManager
import com.jakode.contacts.utils.manager.SelectionManager
import kotlinx.android.synthetic.main.main_popup_layout.view.*
import kotlinx.android.synthetic.main.recent_popup_layout.view.*
import kotlinx.android.synthetic.main.show_user_popup_layout.view.*

object PopupMenu {
    private lateinit var type: Type
    private lateinit var view: View

    private var userInfo: UserInfo? = null
    private var userRecent: Recent? = null
    private var selectionManager: SelectionManager? = null
    private var resentUserManager: ResentUserManager? = null
    private var buttonBox: ButtonBox? = null

    enum class Type {
        MAIN_POPUP, RECENT_POPUP, SELECTION_MODE_POPUP, SHOW_USER_POPUP
    }

    // PopupWindow display method
    @SuppressLint("InflateParams")
    fun show(
        type: Type,
        userInfo: UserInfo?,
        userRecent: Recent?,
        view: View,
        x: Int,
        y: Int,
        selectionManager: SelectionManager?,
        resentUserManager: ResentUserManager?,
        buttonBox: ButtonBox?
    ) {
        // Initialize
        initialize(type, userInfo, userRecent, view, selectionManager, resentUserManager, buttonBox)

        // Create a View object yourself through inflater
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = when (type) {
            Type.MAIN_POPUP -> inflater.inflate(R.layout.main_popup_layout, null)
            Type.RECENT_POPUP -> inflater.inflate(R.layout.recent_popup_layout, null)
            Type.SHOW_USER_POPUP -> inflater.inflate(R.layout.show_user_popup_layout, null)
            Type.SELECTION_MODE_POPUP -> inflater.inflate(R.layout.selection_popup_layout, null)
        }

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
        clickListener(popupView, popupWindow)
    }

    private fun clickListener(popupView: View, popupWindow: PopupWindow) {
        when (type) {
            Type.MAIN_POPUP -> {
                popupView.delete.setOnClickListener {
                    popupWindow.dismiss()
                    if (selectionManager!!.getItemCount() != 0) {
                        selectionManager!!.onContactAction(true)
                        buttonBox!!.hideShareButton()
                    } else {
                        Toast.makeText(
                            view.context,
                            view.context.getString(R.string.add_first),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                popupView.share.setOnClickListener {
                    popupWindow.dismiss()
                    if (selectionManager!!.getItemCount() != 0) {
                        selectionManager!!.onContactAction(true)
                        buttonBox!!.hideDeleteButton()
                    } else {
                        Toast.makeText(
                            view.context,
                            view.context.getString(R.string.add_first),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            Type.RECENT_POPUP -> {
                popupView.clear.setOnClickListener {
                    popupWindow.dismiss()
                    resentUserManager!!.remove(resentUserManager!!.pos)
                    AppRepository(view.context).deleteRecent(userRecent!!)
                }

                popupView.clear_all.setOnClickListener {
                    popupWindow.dismiss()
                    resentUserManager!!.removeAll()
                    AppRepository(view.context).deleteAllRecent()
                }
            }
            Type.SHOW_USER_POPUP -> {
                popupView.delete_user.setOnClickListener {
                    popupWindow.dismiss()
                    BottomSheet(
                        BottomSheet.Type.BOTTOM_DELETE,
                        view.context as Activity,
                        R.style.BottomSheetDialogTheme,
                        userInfo!!
                    ).show()
                }

//                popupView.block.setOnClickListener {
//                    popupWindow.dismiss()
//                    Toast.makeText(view.context, "block user", Toast.LENGTH_SHORT).show()
//                }
            }
            Type.SELECTION_MODE_POPUP -> {
//                popupView.create_group.setOnClickListener {
//                    popupWindow.dismiss()
//                    Toast.makeText(view.context, "create group", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }

    private fun initialize(
        type: Type,
        userInfo: UserInfo?,
        userRecent: Recent?,
        view: View,
        selectionManager: SelectionManager?,
        resentUserManager: ResentUserManager?,
        buttonBox: ButtonBox?
    ) {
        this.type = type
        this.userInfo = userInfo
        this.userRecent = userRecent
        this.view = view
        this.selectionManager = selectionManager
        this.resentUserManager = resentUserManager
        this.buttonBox = buttonBox
    }
}