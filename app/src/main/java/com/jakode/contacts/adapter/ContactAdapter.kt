package com.jakode.contacts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.ui.fragments.MainFragmentDirections
import com.jakode.contacts.utils.ImageUtil
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactAdapter(private val users: ArrayList<UserInfo>) :
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
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val cover: RoundedImageView by lazy { itemView.contact_cover }
        val divider: View by lazy { itemView.divider }

        val name: TextView by lazy { itemView.contact_name }
        val phone: TextView by lazy { itemView.contact_phone }

        fun setData(userInfo: UserInfo) {
            val coverName = userInfo.profile.cover
            if (coverName.contains("cover")) {
                ImageUtil.setDefaultImage(context, cover, coverName)
            } else {
                val uri = ImageUtil.loadFilePrivate(context, coverName).toUri()
                ImageUtil.setImage(uri, cover)
            }
            val name = "${userInfo.user.name.firstName} ${userInfo.user.name.lastName}"
            this.name.text = name
            phone.text = userInfo.phones[0]
        }

        override fun onClick(view: View?) {
            val action = MainFragmentDirections.actionMainFragmentToShowUserFragment(users[adapterPosition])
            view!!.findNavController().navigate(action)
        }
    }
}