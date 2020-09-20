package com.jakode.contacts.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.Search
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.utils.manager.SearchHistoryManager
import kotlinx.android.synthetic.main.resent_search_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchHistoryAdapter(
    private var list: ArrayList<Search>,
    private var appRepository: AppRepository,
    private var searchHistoryManager: SearchHistoryManager
) :
    RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.resent_search_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Init
        holder.setData(list[position])

        // Onclick listener
        holder.itemView.setOnClickListener(holder)
        holder.remove.setOnClickListener(holder)
    }

    override fun getItemCount() = list.size

    fun insertItem(item: Search) {
        appRepository.insertSearch(item)

        list.add(0, item)
        notifyItemInserted(0)
    }

    fun removeItem(position: Int, flag: Boolean = true) {
        appRepository.deleteRowSearch(list[position])

        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)

        if (list.isEmpty() && flag) searchHistoryManager.nothingInput()
    }

    fun removeAll() {
        appRepository.deleteAllSearch()

        list.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val remove: ImageView by lazy { itemView.remove_icon }

        val date: TextView by lazy { itemView.date }
        val name: TextView by lazy { itemView.name }

        fun setData(searchHis: Search) {
            date.text = getDate(searchHis.date)
            name.text = searchHis.query
        }

        @SuppressLint("SimpleDateFormat")
        private fun getDate(date: Date?): CharSequence {
            val now = Calendar.getInstance()
            date?.let {
                val mDate = Calendar.getInstance()
                mDate.time = Date(it.time)

                return if (mDate.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH) == 0) {
                    val format = "HH:mm"
                    SimpleDateFormat(format).format(it)
                } else {
                    val format = "MM:dd"
                    SimpleDateFormat(format).format(it)
                }
            }
            return ""
        }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.remove_icon -> {
                    removeItem(adapterPosition)
                }
                else -> {
                    val pos = adapterPosition
                    val search = Search(list[pos].query, Date())

                    // Update in search history
                    removeItem(pos)
                    insertItem(search)
                    // Set in searchView
                    searchHistoryManager.setQuery(search.query)
                }
            }
        }
    }
}