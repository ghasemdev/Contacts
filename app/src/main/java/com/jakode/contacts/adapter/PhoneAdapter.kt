package com.jakode.contacts.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import kotlinx.android.synthetic.main.phone_list_item.view.*

private const val TAG = "PHONE_ERROR"

class PhoneAdapter(
    private var phones: ArrayList<String>,
    private val icon: View,
    private val divider: View
) :
    RecyclerView.Adapter<PhoneAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var perViewHolder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.phone_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Animation
        holder.itemView.animation = AnimationUtils.loadAnimation(context, R.anim.recycler_fall_down)

        // Init
        holder.phone.setText(phones[position])

        // OnClickListener
        holder.phone.addTextChangedListener(holder)
        holder.remove.setOnClickListener(holder)

        // IME
        if (itemCount > 1) {
            perViewHolder.phone.imeOptions = EditorInfo.IME_ACTION_NEXT
        }
        perViewHolder = holder
    }

    override fun getItemCount() = phones.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, TextWatcher {
        val phone: EditText by lazy { itemView.phone }
        val remove: ImageView by lazy { itemView.phone_remove_icon }

        override fun onClick(v: View?) {
            removeItem(adapterPosition)
            // Invisible add icon
            if (phones.isEmpty()) iconHidden()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            phones[adapterPosition] = s.toString()
        }
    }

    // Add Item
    fun addItem() {
        this.phones.add("")
        notifyItemInserted(phones.size - 1)
    }

    // Remove item
    private fun removeItem(position: Int) {
        try {
            phones.removeAt(position)
        } catch (e: Exception) {
            Log.e(TAG, "removeItem: ${e.stackTrace}")
        }

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, phones.size)
    }

    fun iconDisplay() {
        icon.visibility = View.VISIBLE
        divider.visibility = View.INVISIBLE
    }

    private fun iconHidden() {
        icon.visibility = View.INVISIBLE
        divider.visibility = View.VISIBLE
    }
}