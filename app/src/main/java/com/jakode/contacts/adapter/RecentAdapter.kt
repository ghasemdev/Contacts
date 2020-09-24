package com.jakode.contacts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.Recent
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.ui.fragments.MainFragmentDirections
import com.jakode.contacts.utils.ImageUtil
import com.jakode.contacts.utils.Intents
import com.jakode.contacts.utils.date.DateTime
import com.jakode.contacts.utils.dialog.PopupMenu
import com.jakode.contacts.utils.manager.ResentUserManager
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.recent_list_item.view.*
import java.util.*

class RecentAdapter(
    private val list: ArrayList<Recent>,
    private val recent: TextView,
    private val viewAll: TextView,
    private val recentList: RecyclerView
) :
    RecyclerView.Adapter<RecentAdapter.ViewHolder>(), ResentUserManager {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recent_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Init
        holder.setData(list[position])

        // OnClick listener
        onClickListener(holder)
    }

    private fun onClickListener(holder: ViewHolder) {
        holder.view.setOnClickListener(holder)
        holder.more.setOnClickListener(holder)
        holder.call.setOnClickListener(holder)
        holder.massage.setOnClickListener(holder)
    }

    override fun getItemCount() = list.size

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)

        if (list.isEmpty()) hideRecent()
    }

    private fun hideRecent() {
        recent.visibility = View.GONE
        viewAll.visibility = View.GONE
        recentList.visibility = View.GONE
    }

    fun removeAllItem() {
        list.clear()
        notifyDataSetChanged()

        if (list.isEmpty()) hideRecent()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private lateinit var userInfo: UserInfo

        val view: ConstraintLayout by lazy { itemView.recent_view }
        val cover: RoundedImageView by lazy { itemView.recent_cover }

        val more: ImageView by lazy { itemView.recent_more }
        val call: ImageView by lazy { itemView.recent_call }
        val massage: ImageView by lazy { itemView.recent_massage }

        val firstName: TextView by lazy { itemView.recent_first_name }
        val lastName: TextView by lazy { itemView.recent_last_name }
        val recentTime: TextView by lazy { itemView.recent_time }

        fun setData(recent: Recent) {
            userInfo = AppRepository(context).findUser(recent.userId.toString())

            // Cover
            cover(userInfo)
            // Name
            name(userInfo)
            // Time
            time(recent)
        }

        private fun time(recent: Recent) {
            val from = Calendar.getInstance().apply { timeInMillis = recent.date!!.time }
            recentTime.text = getTime(from)
        }

        private fun getTime(from: Calendar): String {
            val time = DateTime.between(from, Calendar.getInstance()).toString().split(" ")
                .map { it.toInt() }
            return when {
                time[0] > 0 -> "${time[0]} ${context.getString(R.string.year_ago)}"
                time[1] > 0 -> "${time[1]} ${context.getString(R.string.month_ago)}"
                time[2] > 0 -> "${time[2]} ${context.getString(R.string.day_ago)}"
                time[3] > 0 -> "${time[3]} ${context.getString(R.string.hour_ago)}"
                time[4] > 0 -> "${time[4]} ${context.getString(R.string.mins_ago)}"
                time[5] > 0 -> "${time[5]} ${context.getString(R.string.sec_ago)}"
                else -> throw Exception("list size more than 6 and never happen")
            }
        }

        private fun name(userInfo: UserInfo) {
            val (firstName, lastName) = userInfo.user.name.split(";;")
            this.firstName.text = firstName
            this.lastName.text = lastName
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
            when (view?.id) {
                R.id.recent_more -> {
                    pos = adapterPosition
                    PopupMenu.show(
                        PopupMenu.Type.RECENT_POPUP,
                        userInfo = null,
                        list[adapterPosition],
                        view,
                        x = 0,
                        y = -125,
                        selectionManager = null,
                        this@RecentAdapter,
                        buttonBox = null
                    )
                }
                R.id.recent_call -> {
                    Intents.dialPhoneNumber(context, userInfo.phones[0], userInfo.user.id)
                }
                R.id.recent_massage -> {
                    Intents.composeSmsMessage(context, userInfo.phones[0], userInfo.user.id)
                }
                else -> {
                    val action =
                        MainFragmentDirections.actionMainFragmentToShowUserFragment(userInfo)
                    view!!.findNavController().navigate(action)
                }
            }
        }
    }

    override var pos = -1
    override fun remove(position: Int) = removeItem(position)
    override fun removeAll() = removeAllItem()
}