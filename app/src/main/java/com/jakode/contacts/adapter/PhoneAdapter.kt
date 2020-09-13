package com.jakode.contacts.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakode.contacts.R
import com.jakode.contacts.adapter.model.Item
import com.jakode.contacts.utils.Intents
import kotlinx.android.synthetic.main.input_phone_list_item.view.*
import kotlinx.android.synthetic.main.show_phone_list_item.view.*

private const val TAG = "PHONE_ERROR"

class PhoneAdapter(
    private var phones: ArrayList<Item>,
    private val icon: View? = null,
    private val divider: View? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    private var perViewHolder: InputHolder? = null
    private var error = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            0 -> InputHolder(
                LayoutInflater.from(context).inflate(R.layout.input_phone_list_item, parent, false)
            )
            else -> ShowHolder(
                LayoutInflater.from(context).inflate(R.layout.show_phone_list_item, parent, false)
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
                    perViewHolder?.phone?.imeOptions = EditorInfo.IME_ACTION_NEXT
                }
                perViewHolder = holder

                // OnClickListener
                holder.onClick()
            }
            else -> {
                // Init
                (holder as ShowHolder).setData(phones[position].item)

                // OnClick
                holder.onClick()
            }
        }
    }

    override fun getItemCount() = phones.size
    override fun getItemViewType(position: Int) = phones[position].type

    inner class ShowHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val phone: TextView by lazy { itemView.show_phone }

        val call: ImageView by lazy { itemView.call_icon }
        val massage: ImageView by lazy { itemView.massage_icon }
        val duo: ImageView by lazy { itemView.duo_icon }

        fun setData(phone: String) {
            this.phone.text = phone
        }

        fun onClick() {
            call.setOnClickListener(this)
            massage.setOnClickListener(this)
            duo.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.call_icon -> {
                    Intents.dialPhoneNumber(context, phone.text.toString())
                }
                R.id.massage_icon -> {
                    Intents.composeSmsMessage(context, phone.text.toString())
                }
                R.id.duo_icon -> {
                    Intents.dialGoogleDuo(context, phone.text.toString())
                }
                else -> {
                    Intents.dialPhoneNumber(context, phone.text.toString())
                }
            }
        }
    }

    inner class InputHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val phone: TextInputEditText by lazy { itemView.phone }
        val tILPhone: TextInputLayout by lazy { itemView.TIL_phone }
        val remove: ImageView by lazy { itemView.phone_remove_icon }

        fun setData(position: Int) {
            if (phones[position].item.contains("ERROR")) { // Handel incorrect phone
                tILPhone.error = context.resources.getString(R.string.phone_error)
                phones[position].item =
                    phones[position].item.substring(5, phones[position].item.length)
                this.phone.setText(phones[position].item)
                error = true
            } else {
                this.phone.setText(phones[position].item)
            }
        }

        fun onClick() {
            remove.setOnClickListener {
                removeItem(adapterPosition)
                // Invisible add icon
                if (phones.isEmpty()) iconHidden()
            }

            phone.addTextChangedListener {
                if (error) { // Remove error massage when text change
                    tILPhone.isErrorEnabled = false
                    error = false
                }
                phones[adapterPosition].item = it.toString() // Update list
            }
        }
    }

    // Add Item
    fun addItem() {
        this.phones.add(Item(0, ""))
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
        icon!!.visibility = View.VISIBLE
        divider!!.visibility = View.INVISIBLE
    }

    private fun iconHidden() {
        icon!!.visibility = View.INVISIBLE
        divider!!.visibility = View.VISIBLE
    }
}