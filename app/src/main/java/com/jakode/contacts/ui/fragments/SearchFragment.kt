package com.jakode.contacts.ui.fragments

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakode.contacts.R
import com.jakode.contacts.adapter.SearchAdapter
import com.jakode.contacts.adapter.SearchHistoryAdapter
import com.jakode.contacts.data.model.Search
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.databinding.FragmentSearchBinding
import com.jakode.contacts.ui.SearchActivity
import com.jakode.contacts.utils.ButtonBox
import com.jakode.contacts.utils.Intents
import com.jakode.contacts.utils.dialog.BottomSheet
import com.jakode.contacts.utils.dialog.PopupMenu
import com.jakode.contacts.utils.manager.OnBackPressedListener
import com.jakode.contacts.utils.manager.SearchHistoryManager
import com.jakode.contacts.utils.manager.SelectionManager
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment(), SelectionManager, OnBackPressedListener, SearchHistoryManager {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var appRepository: AppRepository
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var buttonBox: ButtonBox

    // Animation
    private var requireAnimIn = true
    private var requireAnimOut = true

    private var users = ArrayList<UserInfo>()
    private var searchList = ArrayList<Search>()
    private lateinit var selectedContacts: List<UserInfo>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            (activity as SearchActivity).setIoBack(this)
        } catch (e: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement IOnBackPressed")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

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
            hideKeyboard(requireContext(), it)
            (activity as SearchActivity).onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init repository
        appRepository = AppRepository(requireContext())

        // Init recent search
        val list = appRepository.getAllSearch()
        list.forEach { searchList.add(it) }

        // Init searchView
        initSearchView()

        // Search list
        initRecycler()

        if (searchList.isNotEmpty()) {
            binding.errorText.visibility = View.GONE
            binding.titleText.text = getString(R.string.recent_search)
            binding.removeAll.visibility = View.VISIBLE
        }

        // Init clickable
        clickListener()

        // Init button box
        buttonBox = ButtonBox(binding.delete, binding.deleteIcon, binding.share, binding.shareIcon)
    }

    private fun initSearchView() {
        // open search view
        binding.search.onActionViewExpanded()

        // search view text change listener
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.search.clearFocus()
                // Insert in search history
                query?.let { it ->
                    if (it.isNotEmpty()) {
                        val item = Search(it, Date())
                        var pos: Int
                        // If query has exist updated
                        searchList.map { m -> m.query }.also { list -> pos = list.indexOf(it) }
                        if (pos > -1) searchHistoryAdapter.removeItem(pos, false)
                        searchHistoryAdapter.insertItem(item)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText.isNotEmpty()) { // Searching
                        users = appRepository.findUsers(it)
                        if (users.isNotEmpty()) { // Find something
                            findSomething(users, newText)
                        } else { // Nothing find
                            nothingFind()
                        }
                    } else { // Nothing input
                        nothingInput()
                    }
                }
                return true
            }
        })

        // set search info from xml and enabled voice
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.search.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        }
    }

    private fun findSomething(
        list: ArrayList<UserInfo>,
        newText: String?
    ) {
        visibilityOnFindSomething()
        binding.titleText.text = getString(R.string.app_name)
        val text = "${list.size} ${getString(R.string.contact_find)}"
        binding.descriptionText.text = text
        searchAdapter.setItem(list, newText)
    }

    private fun nothingFind() {
        visibilityOnNothingFind()
        binding.errorText.text = getText(R.string.empty_search)
    }

    override fun nothingInput() {
        binding.historyList.visibility = View.VISIBLE
        val list = appRepository.getAllSearch()
        searchList.clear()
        list.forEach { searchList.add(it) }

        if (searchList.isEmpty()) {
            historyEmpty()
            binding.errorText.text = getText(R.string.empty_recent_search)
        } else {
            historyNotEmpty()
            binding.titleText.text = getText(R.string.recent_search)
        }
        searchAdapter.setItem(ArrayList(), null)
    }

    override fun setQuery(query: String) {
        binding.search.setQuery(query, false)
    }

    private fun visibilityOnFindSomething() {
        binding.titleText.visibility = View.VISIBLE
        binding.descriptionText.visibility = View.VISIBLE
        binding.errorText.visibility = View.INVISIBLE
        binding.searchList.visibility = View.VISIBLE
        binding.historyList.visibility = View.GONE
        binding.removeAll.visibility = View.GONE
    }

    private fun visibilityOnNothingFind() {
        binding.titleText.visibility = View.GONE
        binding.descriptionText.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.searchList.visibility = View.GONE
        binding.historyList.visibility = View.GONE
        binding.removeAll.visibility = View.GONE
    }

    private fun historyEmpty() {
        binding.titleText.visibility = View.GONE
        binding.descriptionText.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.removeAll.visibility = View.GONE
    }

    private fun historyNotEmpty() {
        binding.titleText.visibility = View.VISIBLE
        binding.descriptionText.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.removeAll.visibility = View.VISIBLE
    }

    private fun initRecycler() {
        searchHistoryAdapter = SearchHistoryAdapter(searchList, appRepository, this)
        binding.historyList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchHistoryAdapter
        }

        searchAdapter = SearchAdapter(users, this)
        binding.searchList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }
    }

    private fun clickListener() {
        // Delete click listener
        binding.delete.setOnClickListener {
            selectedContacts = searchAdapter.getSelectedContacts()
            if (selectedContacts.isNotEmpty()) {
                BottomSheet(
                    BottomSheet.Type.BOTTOM_SELECT_TO_DELETE,
                    requireActivity(),
                    R.style.BottomSheetDialogTheme,
                    users = selectedContacts,
                    selectionManager = this
                ).show()
            }
        }

        // Share click listener
        binding.share.setOnClickListener {
            selectedContacts = searchAdapter.getSelectedContacts()
            if (selectedContacts.isNotEmpty()) {
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
        }

        // Select all button
        binding.selectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                searchAdapter.selectCheckBoxes()
            } else {
                searchAdapter.deselectCheckBoxes()
            }
            initSelectionHeader()
        }

        // Clear history
        binding.removeAll.setOnClickListener {
            historyEmpty()
            binding.errorText.text = getText(R.string.empty_recent_search)
            searchHistoryAdapter.removeAll()
        }
    }

    override fun onResume() {
        super.onResume()
        // Handel intent
        activity?.intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also {
                binding.search.setQuery(it, true)
            }
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.show_user_menu, menu)
        // Hide more icon
        binding.toolbar.menu.getItem(0).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_user_more -> {
                val anchor: View = requireView().findViewById(R.id.show_user_more)
                val x: Int
                val y: Int
                if (Locale.getDefault().language == "fa") {
                    x = -120
                    y = -125
                } else {
                    x = 0
                    y = -125
                }
                PopupMenu.show(
                    PopupMenu.Type.SELECTION_MODE_POPUP,
                    userInfo = null,
                    userRecent = null,
                    anchor,
                    x,
                    y,
                    selectionManager = null,
                    resentUserManager = null,
                    buttonBox = null
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override var selectionMode: Boolean = false
    override fun getItemCount() = users.size

    override fun removeUsers(selectedUser: List<UserInfo>) {
        if (getItemCount() == selectedUser.size) nothingFind()
        else {
            val text = "${getItemCount() - selectedUser.size} ${getString(R.string.contact_find)}"
            binding.descriptionText.text = text
        }
        searchAdapter.removeContacts(selectedUser)
    }

    override fun onContactAction(isSelected: Boolean) {
        super.onContactAction(isSelected)

        if (isSelected) { // Selection mode enabled
            if (requireAnimIn) {
                boxButtonShow()     // Box button show
                requireAnimIn = false
                requireAnimOut = true
            }
            // Show all checkbox
            searchAdapter.showCheckBoxes()

            initSelectionHeader()

            // Hidden toolbar item
            toolbarItemHidden()
        } else { // Selection mode disabled
            if (requireAnimOut) {
                boxButtonHidden()   // Box button hidden
                requireAnimIn = true
                requireAnimOut = false
            }

            // Show toolbar item
            toolbarItemShow()

            // Unable selection
            searchAdapter.hideCheckBoxes()

            // Visible button box
            Handler(Looper.getMainLooper()).postDelayed({
                buttonBox.showButtons()
            }, 200)
        }
    }

    private fun toolbarItemHidden() {
        binding.toolbar.navigationIcon = null
        binding.search.visibility = View.GONE
        binding.toolbar.menu.getItem(0).isVisible = true
        binding.selection.visibility = View.VISIBLE

        // Select all coordinated with body selection
        selectedContacts = searchAdapter.getSelectedContacts()
        when (selectedContacts.size) {
            0 -> binding.selectAll.isChecked = false
            getItemCount() -> binding.selectAll.isChecked = true
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun toolbarItemShow() {
        binding.toolbar.navigationIcon = requireContext().getDrawable(R.drawable.ic_back)
        binding.search.visibility = View.VISIBLE
        binding.toolbar.menu.getItem(0).isVisible = false
        binding.selectAll.isChecked = false
        binding.selection.visibility = View.GONE
    }

    private fun initSelectionHeader() {
        binding.selectedUsers.text = selectedUser()
    }

    private fun selectedUser(): String {
        selectedContacts = searchAdapter.getSelectedContacts()
        return if (selectedContacts.isNotEmpty()) {
            "${selectedContacts.size} ${resources.getString(R.string.selected)}"
        } else {
            resources.getString(R.string.select_contacts)
        }
    }

    private fun boxButtonShow() {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.translate_in).apply {
            setTarget(binding.boxButton)
            start()
        }
    }

    private fun boxButtonHidden() {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.translate_out).apply {
            setTarget(binding.boxButton)
            start()
        }
    }

    override fun onBackPressed(): Boolean {
        return if (selectionMode) {
            onContactAction(false)
            false
        } else {
            true
        }
    }
}