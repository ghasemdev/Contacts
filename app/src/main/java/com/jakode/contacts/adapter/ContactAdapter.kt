package com.jakode.contacts.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.UserTest
import com.jakode.contacts.utils.ImageSetter
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactAdapter(private val list: ArrayList<UserTest>) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false)
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set background
        if (position == 0) holder.itemView.background = context.getDrawable(R.drawable.top_radius)

        // Init
        ImageSetter.set(list[position].cover, holder.cover)
        if (position == itemCount - 1) holder.divider.visibility = View.INVISIBLE
        val name = "${list[position].firstName} ${list[position].lastName}"
        holder.name.text = name
        holder.phone.text = list[position].phone

        // Onclick listener
        holder.itemView.setOnClickListener(holder)
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val cover: RoundedImageView by lazy { itemView.contact_cover }
        val divider: View by lazy { itemView.divider }

        val name: TextView by lazy { itemView.contact_name }
        val phone: TextView by lazy { itemView.contact_phone }

        override fun onClick(v: View?) {
            Toast.makeText(context, "contact body", Toast.LENGTH_SHORT).show()
        }
    }
}