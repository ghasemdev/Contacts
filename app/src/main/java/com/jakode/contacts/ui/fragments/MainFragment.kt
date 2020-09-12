package com.jakode.contacts.ui.fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.adapter.ContactAdapter
import com.jakode.contacts.adapter.RecentAdapter
import com.jakode.contacts.databinding.FragmentMainBinding
import com.jakode.contacts.utils.Data
import com.jakode.contacts.utils.DrawerManager
import com.jakode.contacts.utils.ImageSetter
import com.jakode.contacts.utils.PopupMenu
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var drawerManager: DrawerManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerManager = activity as DrawerManager
        } catch (e: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement DrawerLocker")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)

        // Init toolbar
        initToolbar()

        return binding.root
    }

    private fun initToolbar() {
        // Set toolbar and menu
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        // Set action for menu icon
        binding.toolbar.setNavigationOnClickListener {
            drawerManager.openDrawer()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init cover
        ImageSetter.set(
            "https://i.redd.it/hfdbbih4nou41.jpg",
            binding.toolbarHeader.myCover
        )

        // RecyclerViews
        initRecycler()

        // Init clickable
        clickListener()
    }

    private fun initRecycler() {
        // Recent list
        binding.toolbarHeader.recentList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = RecentAdapter(Data.recentUsers())
        }

        // Contact list
        binding.contactList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ContactAdapter(Data.contactUsers())
        }

        // Handel Swipe and Move item of contact
        itemTouchHelper()
    }

    private fun itemTouchHelper() {
        ItemTouchHelper(SimpleCallBack()).apply {
            attachToRecyclerView(binding.contactList)
        }
    }

    // CallBack for swipe
    inner class SimpleCallBack :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            when (direction) {
                ItemTouchHelper.RIGHT -> {
                    Toast.makeText(context, "$position call", Toast.LENGTH_SHORT).show()
                }
                ItemTouchHelper.LEFT -> {
                    Toast.makeText(context, "$position massage", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onChildDraw( // Set icon and color for swipe
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            RecyclerViewSwipeDecorator.Builder(
                c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
            )
                .addSwipeRightBackgroundColor(Color.parseColor("#15b76c"))
                .addSwipeRightActionIcon(R.drawable.ic_call)
                .setSwipeRightActionIconTint(Color.WHITE)
                .addSwipeRightLabel(resources.getString(R.string.call))
                .setSwipeRightLabelColor(Color.WHITE)
                .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
                .addSwipeLeftBackgroundColor(Color.parseColor("#16abe3"))
                .addSwipeLeftActionIcon(R.drawable.ic_chat)
                .setSwipeLeftActionIconTint(Color.WHITE)
                .addSwipeLeftLabel(resources.getString(R.string.massage))
                .setSwipeLeftLabelColor(Color.WHITE)
                .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
                .setIconHorizontalMargin(TypedValue.COMPLEX_UNIT_DIP, 25)
                .create()
                .decorate()
        }
    }

    private fun clickListener() {
        // Cover click listener
        binding.toolbarHeader.myCover.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAdminFragment()
            findNavController().navigate(action)
        }

        // Recent click listener
        binding.toolbarHeader.viewAll.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToRecentFragment()
            findNavController().navigate(action)
        }

        // Fab button click listener
        binding.fabButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddUserFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                // Safe args
                val action = MainFragmentDirections.actionMainFragmentToSearchFragment()
                findNavController().navigate(action)
                true
            }
            R.id.more -> {
                // Open more options
                val anchor: View = requireView().findViewById(R.id.more)
                PopupMenu.show(PopupMenu.Type.MAIN_POPUP, anchor, 0, -125)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}