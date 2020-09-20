package com.jakode.contacts.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.ui.fragments.SearchFragmentDirections
import com.jakode.contacts.utils.ImageUtil
import com.jakode.contacts.utils.manager.SelectionManager
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.contact_list_item.view.*

class SearchAdapter(
    private var users: ArrayList<UserInfo>,
    val selectionManager: SelectionManager? = null
) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private lateinit var context: Context

    private var query: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Init
        holder.setData(users[position])

        // Onclick listener
        holder.itemView.setOnClickListener(holder)
        holder.itemView.setOnLongClickListener(holder)
    }

    override fun getItemCount() = users.size

    fun setItem(users: ArrayList<UserInfo>, query: String?) {
        this.query = query
        this.users = users
        notifyDataSetChanged()
    }

    fun getSelectedContacts(): List<UserInfo> {
        val selectedContacts = ArrayList<UserInfo>()
        users.filter { it.isSelected }.forEach { selectedContacts.add(it) }
        return selectedContacts
    }

    fun showCheckBoxes() {
        // show all checkbox
        users.forEach { it.isVisible = true }.also {
            notifyDataSetChanged()
        }
    }

    fun hideCheckBoxes() {
        users.apply {
            filter { it.isVisible }.forEach { it.isVisible = false }
            filter { it.isSelected }.forEach { it.isSelected = false }
        }.also { notifyDataSetChanged() }
    }

    fun selectCheckBoxes() {
        users.filter { !it.isSelected }.forEach { it.isSelected = true }.also {
            notifyDataSetChanged()
        }
    }

    fun deselectCheckBoxes() {
        users.filter { it.isSelected }.forEach { it.isSelected = false }.also {
            notifyDataSetChanged()
        }
    }

    fun removeContacts(selectedUser: List<UserInfo>) {
        users.removeAll(selectedUser)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val cover: RoundedImageView by lazy { itemView.contact_cover }
        val divider: View by lazy { itemView.divider }
        val checkBox: CheckBox by lazy { itemView.checkBox }

        val name: TextView by lazy { itemView.contact_name }
        val phone: TextView by lazy { itemView.contact_phone }

        fun setData(userInfo: UserInfo) {
            // Cover
            cover(userInfo)
            // Name
            name(userInfo)
            // Phone
            phone(userInfo)
            // Checkbox
            initSelectionOption(userInfo)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun initSelectionOption(userInfo: UserInfo) {
            checkBox.isChecked = if (userInfo.isSelected) {
                itemView.background =
                    if (adapterPosition == 0) context.getDrawable(R.drawable.selected_top_background)
                    else context.getDrawable(R.drawable.selected_background)
                true
            } else {
                itemView.setBackgroundResource(android.R.color.transparent)
                false
            }

            checkBox.visibility = if (userInfo.isVisible) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        private fun phone(userInfo: UserInfo) {
            val phone = userInfo.phones[0]
            if (isNumber(query!!)) { // Is a number highlight
                val start = phone.indexOf(query!!)
                SpannableString(phone).apply {
                    val fcs = ForegroundColorSpan(Color.parseColor("#E53935"))
                    setSpan(fcs, start, start + query!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }.also { this.phone.text = it }
            } else {
                this.phone.text = phone
            }
        }

        private fun name(userInfo: UserInfo) {
            val (firstName, lastName) = userInfo.user.name.split(";;")
            val name = "$firstName $lastName"
            // Set text and color that
            if (!isNumber(query!!)) { // Is a name highlight
                val start = name.indexOf(query!!)
                SpannableString(name).apply {
                    val fcs = ForegroundColorSpan(Color.parseColor("#E53935"))
                    setSpan(fcs, start, start + query!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }.also { this.name.text = it }
            } else {
                this.name.text = name
            }
        }

        private fun cover(userInfo: UserInfo) {
            val coverName = userInfo.profile.cover
            if (coverName.contains("cover")) {
                ImageUtil.setDefaultImage(context, cover, coverName)
            } else {
                val uri = ImageUtil.loadFilePrivate(context, coverName).toUri()
                ImageUtil.setImage(uri, cover)
            }
        }

        override fun onClick(view: View?) {
            if (selectionManager!!.selectionMode) {
                multiSelection()
            } else {
                hideKeyboard(context, view!!)
                val action =
                    SearchFragmentDirections.actionSearchFragmentToShowUserFragment(users[adapterPosition])
                view.findNavController().navigate(action)
            }
        }

        private fun hideKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        private fun isNumber(query: String): Boolean {
            var state = true
            for (c in query) {
                val ascii = c.toInt()
                if (ascii < 48 || ascii > 57) {
                    state = false
                    break
                }
            }
            return state
        }

        override fun onLongClick(view: View?): Boolean {
            hideKeyboard(context, view!!)
            multiSelection()
            return true
        }

        private fun multiSelection() {
            if (users[adapterPosition].isSelected) {
                checkBox.isChecked = false
                users[adapterPosition].isSelected = false
            } else {
                checkBox.isChecked = true
                users[adapterPosition].isSelected = true
            }
            selectionManager!!.onContactAction(true)
        }
    }
}