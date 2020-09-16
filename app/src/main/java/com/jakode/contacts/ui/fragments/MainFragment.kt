package com.jakode.contacts.ui.fragments

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
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
import com.google.android.material.appbar.AppBarLayout
import com.jakode.contacts.R
import com.jakode.contacts.adapter.ContactAdapter
import com.jakode.contacts.adapter.RecentAdapter
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.databinding.FragmentMainBinding
import com.jakode.contacts.utils.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainFragment : Fragment(), SelectionManager, View.OnKeyListener {
    private lateinit var binding: FragmentMainBinding
    private lateinit var drawerManager: DrawerManager
    private lateinit var appRepository: AppRepository
    private lateinit var contactAdapter: ContactAdapter

    // Animation
    private var requireAnimIn = true
    private var requireAnimOut = true

    private var users = ArrayList<UserInfo>()
    private lateinit var selectedContacts: List<UserInfo>

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
        // Init repository
        appRepository = AppRepository(requireContext())

        // Back press
        onBackPressed(view)

        // Init list of users
        users = appRepository.getAllUsers()
        if (users.isEmpty()) binding.emptyAlarm.visibility = View.VISIBLE

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
        contactAdapter = ContactAdapter(users, this)
        binding.contactList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = contactAdapter
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
                    contactAdapter.notifyDataSetChanged()
                    Intents.dialPhoneNumber(requireContext(), users[position].phones[0])
                }
                ItemTouchHelper.LEFT -> {
                    contactAdapter.notifyDataSetChanged()
                    Intents.composeSmsMessage(requireContext(), users[position].phones[0])
                }
            }
        }

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (selectionMode) return 0
            return super.getSwipeDirs(recyclerView, viewHolder)
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

        if (users.isNotEmpty()) { // When users empty delete, share and select dos'nt mean
            // Delete click listener
            binding.delete.setOnClickListener {
                selectedContacts = contactAdapter.getSelectedContacts()
                BottomSheet(
                    BottomSheet.Type.BOTTOM_SELECT_TO_DELETE,
                    requireActivity(),
                    R.style.BottomSheetDialogTheme,
                    users = selectedContacts,
                    selectionManager = this
                ).show()
            }

            // Share click listener
            binding.share.setOnClickListener {
                selectedContacts = contactAdapter.getSelectedContacts()
                if (selectedContacts.size == 1) {
                    BottomSheet(
                        BottomSheet.Type.BOTTOM_SHARE,
                        requireActivity(),
                        R.style.BottomSheetDialogTheme,
                        selectedContacts[0]
                    ).show()
                } else {
                    Intents.sendVCard(requireContext(), selectedContacts)
                }
            }

            // Select all button
            binding.selectAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    contactAdapter.selectCheckBoxes()
                } else {
                    contactAdapter.deselectCheckBoxes()
                }
                onContactAction(true)
            }
        }
    }

    private fun onBackPressed(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(this)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (selectionMode) onContactAction(false) else return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                if (selectionMode) {
                    Toast.makeText(requireContext(), "search", Toast.LENGTH_SHORT).show()
                } else {
                    val action = MainFragmentDirections.actionMainFragmentToSearchFragment()
                    findNavController().navigate(action)
                }
                true
            }
            R.id.more -> {
                val anchor: View = requireView().findViewById(R.id.more)
                if (selectionMode) { // Open more options
                    PopupMenu
                        .show(PopupMenu.Type.SELECTION_MODE_POPUP, userInfo = null, anchor, 0, -125)
                } else {
                    PopupMenu.show(PopupMenu.Type.MAIN_POPUP, userInfo = null, anchor, 0, -125)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override var selectionMode: Boolean = false

    override fun onContactAction(isSelected: Boolean) {
        super.onContactAction(isSelected)

        if (isSelected) { // Selection mode enabled
            if (requireAnimIn) {
                fabHidden()         // Fab hidden
                boxButtonShow()     // Box button show
                requireAnimIn = false
                requireAnimOut = true
            }
            // Show all checkbox
            contactAdapter.showCheckBoxes()

            // Showing selection header
            selectionHeaderShow()
            initSelectionHeader()

            // Hidden toolbar item
            toolbarItemHidden()
        } else { // Selection mode disabled
            if (requireAnimOut) {
                fabShow()           // Fab show
                boxButtonHidden()   // Box button hidden
                requireAnimIn = true
                requireAnimOut = false
            }
            // Hidden selection header
            selectionHeaderHidden()

            // Show toolbar item
            toolbarItemShow()

            // Unable selection
            usersUpdate()
        }
    }

    private fun usersUpdate() {
        // remove contacts from recycler
        val newUsers = appRepository.getAllUsers()
        if (users.size != newUsers.size) contactAdapter.removeContacts(selectedContacts)
        if (newUsers.isEmpty()) binding.emptyAlarm.visibility = View.VISIBLE
        contactAdapter.hideCheckBoxes()
    }

    private fun toolbarItemHidden() {
        binding.toolbar.navigationIcon = null
        binding.selectAll.visibility = View.VISIBLE
        binding.checkboxText.visibility = View.VISIBLE

        // Toolbar Text
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                if (state == State.EXPANDED) { // Open
                    binding.selectedUsers.visibility = View.GONE
                } else if (state == State.COLLAPSED) { // Closed
                    binding.selectedUsers.visibility = View.VISIBLE
                    binding.selectedUsers.text = selectedUser()
                }
            }
        })

        // Select all coordinated with body selection
        selectedContacts = contactAdapter.getSelectedContacts()
        when (selectedContacts.size) {
            0 -> binding.selectAll.isChecked = false
            users.size -> binding.selectAll.isChecked = true
        }
        drawerManager.lockDrawer()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun toolbarItemShow() {
        binding.toolbar.navigationIcon = requireContext().getDrawable(R.drawable.ic_menu)
        binding.selectAll.visibility = View.GONE
        binding.checkboxText.visibility = View.GONE
        binding.selectedUsers.visibility = View.GONE
        drawerManager.unlockDrawer()
    }

    private fun initSelectionHeader() {
        binding.toolbarHeaderSelection.contactSelect.text = selectedUser()
    }

    private fun selectedUser(): String {
        selectedContacts = contactAdapter.getSelectedContacts()
        return if (selectedContacts.isNotEmpty()) {
            "${selectedContacts.size} ${resources.getString(R.string.selected)}"
        } else {
            resources.getString(R.string.select_contacts)
        }
    }

    private fun selectionHeaderShow() {
        binding.toolbarHeader.root.visibility = View.INVISIBLE
        binding.toolbarHeaderSelection.root.visibility = View.VISIBLE
    }

    private fun selectionHeaderHidden() {
        binding.toolbarHeader.root.visibility = View.VISIBLE
        binding.toolbarHeaderSelection.root.visibility = View.INVISIBLE
    }

    private fun fabHidden() {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_in).apply {
            setTarget(binding.fabButton)
            start()
        }
    }

    private fun boxButtonShow() {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.translate_in).apply {
            setTarget(binding.boxButton)
            start()
        }
    }

    private fun fabShow() {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.scale_out).apply {
            setTarget(binding.fabButton)
            start()
        }
    }

    private fun boxButtonHidden() {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.translate_out).apply {
            setTarget(binding.boxButton)
            start()
        }
    }
}