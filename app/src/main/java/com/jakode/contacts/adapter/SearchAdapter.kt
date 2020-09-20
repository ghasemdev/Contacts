package com.jakode.contacts.adapter

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
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.contact_list_item.view.*

class SearchAdapter(private var users: ArrayList<UserInfo>) :
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
    }

    override fun getItemCount() = users.size

    fun setItem(users: ArrayList<UserInfo>, query: String?) {
        this.query = query
        this.users = users
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
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
        }

        private fun phone(userInfo: UserInfo) {
            val phone = userInfo.phones[0]
            if (isNumber(query!!)) {
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
            if (!isNumber(query!!)) {
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
            hideKeyboard(context, view!!)
            val action =
                SearchFragmentDirections.actionSearchFragmentToShowUserFragment(users[adapterPosition])
            view.findNavController().navigate(action)
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
    }
}