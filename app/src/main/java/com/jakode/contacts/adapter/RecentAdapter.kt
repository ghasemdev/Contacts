package com.jakode.contacts.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.Recent
import com.jakode.contacts.utils.ImageSetter
import com.makeramen.roundedimageview.RoundedImageView
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
        ImageSetter.set(list[position].user.cover, holder.cover)
        holder.firstName.text = list[position].user.firstName
        holder.lastName.text = list[position].user.lastName
        holder.recentTime.text = list[position].recentTime

        // Onclick listener
        onClickListener(holder)

        // Set borderless ripple
        borderlessRipple(holder)
    }

    private fun onClickListener(holder: ViewHolder) {
        holder.view.setOnClickListener(holder)
        holder.more.setOnClickListener(holder)
        holder.call.setOnClickListener(holder)
        holder.massage.setOnClickListener(holder)
    }

    private fun borderlessRipple(holder: ViewHolder) {
        val outValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            outValue,
            true
        )
        holder.more.setBackgroundResource(outValue.resourceId)
        holder.call.setBackgroundResource(outValue.resourceId)
        holder.massage.setBackgroundResource(outValue.resourceId)
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val view: ConstraintLayout by lazy { itemView.item_view }
        val cover: RoundedImageView by lazy { itemView.item_cover }

        val more: ImageView by lazy { itemView.item_more }
        val call: ImageView by lazy { itemView.item_call }
        val massage: ImageView by lazy { itemView.item_massage }

        val firstName: TextView by lazy { itemView.item_first_name }
        val lastName: TextView by lazy { itemView.item_last_name }
        val recentTime: TextView by lazy { itemView.item_recent_time }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.item_more -> {
                    Toast.makeText(context, "more", Toast.LENGTH_SHORT).show()
                }
                R.id.item_call -> {
                    Toast.makeText(context, "call", Toast.LENGTH_SHORT).show()
                }
                R.id.item_massage -> {
                    Toast.makeText(context, "massage", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "body", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}