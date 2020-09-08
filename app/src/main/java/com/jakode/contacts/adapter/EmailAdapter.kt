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
import com.google.android.material.textfield.TextInputLayout
import com.jakode.contacts.R
import kotlinx.android.synthetic.main.email_list_item.view.*
import kotlinx.android.synthetic.main.phone_list_item.view.*

private const val TAG = "EMAIL_ERROR"

class EmailAdapter(
    private var emails: ArrayList<String>,
    private val icon: View,
    private val divider: View
) :
    RecyclerView.Adapter<EmailAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var perViewHolder: ViewHolder
    private var error = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.email_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Animation
        holder.itemView.animation = AnimationUtils.loadAnimation(context, R.anim.recycler_fall_down)

        // Init
        if (emails[position].contains("ERROR")) { // Handel incorrect phone
            holder.tILEmail.error = context.resources.getString(R.string.email_error)
            emails[position] = emails[position].substring(5, emails[position].length)
            holder.email.setText(emails[position])
            error = true
        } else {
            holder.email.setText(emails[position])
        }

        // OnClickListener
        holder.email.addTextChangedListener(holder)
        holder.remove.setOnClickListener(holder)

        // IME
        if (itemCount > 1) {
            perViewHolder.email.imeOptions = EditorInfo.IME_ACTION_NEXT
        }
        perViewHolder = holder
    }

    override fun getItemCount() = emails.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, TextWatcher {
        val email: EditText by lazy { itemView.email }
        val tILEmail: TextInputLayout by lazy { itemView.TIL_email }
        val remove: ImageView by lazy { itemView.email_remove_icon }

        override fun onClick(v: View?) {
            removeItem(adapterPosition)
            // Invisible add icon
            if (emails.isEmpty()) iconHidden()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (error) { // Remove error massage when text change
                tILEmail.isErrorEnabled = false
                error = false
            }
        }

        override fun afterTextChanged(s: Editable?) {
            emails[adapterPosition] = s.toString()
        }
    }

    // Add Item
    fun addItem() {
        this.emails.add("")
        notifyItemInserted(emails.size - 1)
    }

    // Remove item
    private fun removeItem(position: Int) {
        try {
            emails.removeAt(position)
        } catch (e: Exception) {
            Log.e(TAG, "removeItem: ${e.stackTrace}")
        }

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, emails.size)
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