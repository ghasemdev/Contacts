package com.jakode.contacts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.Recent
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recent_list_item.view.*

class RecentAdapter(private val list: ArrayList<Recent>) :
    RecyclerView.Adapter<RecentAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recent_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Init
        Picasso.get().load(list[position].user.cover).placeholder(R.drawable.cover_01)
            .into(holder.cover)
        holder.firstName.text = list[position].user.firstName
        holder.lastName.text = list[position].user.lastName
        holder.recentTime.text = list[position].recentTime

        // Onclick listener
        holder.itemView.setOnClickListener(holder)
        holder.more.setOnClickListener(holder)
        holder.call.setOnClickListener(holder)
        holder.massage.setOnClickListener(holder)
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val cover: RoundedImageView by lazy { itemView.cover }

        val more: ImageView by lazy { itemView.more }
        val call: ImageView by lazy { itemView.call }
        val massage: ImageView by lazy { itemView.massage }

        val firstName: TextView by lazy { itemView.first_name }
        val lastName: TextView by lazy { itemView.last_name }
        val recentTime: TextView by lazy { itemView.recent_time }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.more -> {
                    Toast.makeText(context, "more", Toast.LENGTH_SHORT).show()
                }
                R.id.call -> {
                    Toast.makeText(context, "call", Toast.LENGTH_SHORT).show()
                }
                R.id.massage -> {
                    Toast.makeText(context, "massage", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "body", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}