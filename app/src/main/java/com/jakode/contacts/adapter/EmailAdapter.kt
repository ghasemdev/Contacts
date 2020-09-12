package com.jakode.contacts.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jakode.contacts.R
import com.jakode.contacts.adapter.model.Item
import com.jakode.contacts.utils.Intents
import kotlinx.android.synthetic.main.input_email_list_item.view.*
import kotlinx.android.synthetic.main.show_email_list_item.view.*

private const val TAG = "EMAIL_ERROR"

class EmailAdapter(
    private var emails: ArrayList<Item>,
    private val icon: View? = null,
    private val divider: View? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var perViewHolder: InputHolder
    private var error = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            0 -> InputHolder(
                LayoutInflater.from(context).inflate(R.layout.input_email_list_item, parent, false)
            )
            else -> ShowHolder(
                LayoutInflater.from(context).inflate(R.layout.show_email_list_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                // Animation
                (holder as InputHolder).itemView.animation =
                    AnimationUtils.loadAnimation(context, R.anim.recycler_fall_down)

                // Init
                holder.setData(position)

                // IME
                if (itemCount > 1) {
                    perViewHolder.email.imeOptions = EditorInfo.IME_ACTION_NEXT
                }
                perViewHolder = holder

                // OnClickListener
                holder.onClick()
            }
            else -> {
                // Init
                (holder as ShowHolder).setData(emails[position].item)

                // OnClick
                holder.onClick()
            }
        }
    }

    override fun getItemCount() = emails.size
    override fun getItemViewType(position: Int) = emails[position].type

    inner class ShowHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val email: TextView by lazy { itemView.show_email }

        val emailIcon: ImageView by lazy { itemView.email_icon }

        fun setData(email: String) {
            this.email.text = email
        }

        fun onClick() {
            emailIcon.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.email_icon -> {
                    Intents.composeEmail(context, arrayOf(email.text.toString()), "", "")
                }
                else -> {
                    Intents.composeEmail(context, arrayOf(email.text.toString()), "", "")
                }
            }
        }
    }

    inner class InputHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val email: EditText by lazy { itemView.email }
        val tILEmail: TextInputLayout by lazy { itemView.TIL_email }
        val remove: ImageView by lazy { itemView.email_remove_icon }

        fun setData(position: Int) {
            if (emails[position].item.contains("ERROR")) { // Handel incorrect phone
                tILEmail.error = context.resources.getString(R.string.email_error)
                emails[position].item =
                    emails[position].item.substring(5, emails[position].item.length)
                this.email.setText(emails[position].item)
                error = true
            } else {
                this.email.setText(emails[position].item)
            }
        }

        fun onClick() {
            remove.setOnClickListener {
                removeItem(adapterPosition)
                // Invisible add icon
                if (emails.isEmpty()) iconHidden()
            }

            email.addTextChangedListener {
                if (error) { // Remove error massage when text change
                    tILEmail.isErrorEnabled = false
                    error = false
                }
                emails[adapterPosition].item = it.toString() // Update list
            }
        }
    }

    // Add Item
    fun addItem() {
        this.emails.add(Item(0, ""))
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
        icon!!.visibility = View.VISIBLE
        divider!!.visibility = View.INVISIBLE
    }

    private fun iconHidden() {
        icon!!.visibility = View.INVISIBLE
        divider!!.visibility = View.VISIBLE
    }
}