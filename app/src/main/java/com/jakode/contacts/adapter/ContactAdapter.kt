package com.jakode.contacts.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.ui.fragments.MainFragmentDirections
import com.jakode.contacts.utils.ImageUtil
import com.jakode.contacts.utils.SelectionManager
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactAdapter(
    private val users: ArrayList<UserInfo>,
    val selectionManager: SelectionManager
) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false)
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

    fun getSelectedContacts(): List<UserInfo> {
        val selectedContacts = ArrayList<UserInfo>()
        users.filter { it.isSelected }.forEach { selectedContacts.add(it) }
        return selectedContacts
    }

    fun showCheckBox() {
        // show all checkbox
        users.forEach { it.isVisible = true }.also {
            notifyDataSetChanged()
        }
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
                itemView.background = context.getDrawable(R.drawable.selected_ripple_background)
                true
            } else {
                itemView.background = null
                false
            }

            checkBox.visibility = if (userInfo.isVisible) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        private fun phone(userInfo: UserInfo) {
            phone.text = userInfo.phones[0]
        }

        private fun name(userInfo: UserInfo) {
            val name = "${userInfo.user.name.firstName} ${userInfo.user.name.lastName}"
            this.name.text = name
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
            if (selectionManager.selectionMode) {
                multiSelection()
            } else {
                val action =
                    MainFragmentDirections.actionMainFragmentToShowUserFragment(users[adapterPosition])
                view!!.findNavController().navigate(action)
            }
        }

        override fun onLongClick(view: View?): Boolean {
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
            selectionManager.onContactAction(true)
        }
    }
}