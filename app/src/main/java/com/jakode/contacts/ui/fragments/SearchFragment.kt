package com.jakode.contacts.ui.fragments

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakode.contacts.R
import com.jakode.contacts.adapter.SearchAdapter
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.databinding.FragmentSearchBinding
import com.jakode.contacts.utils.manager.OnBackPressedListener

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var appRepository: AppRepository
    private lateinit var onBackPressedListener: OnBackPressedListener
    private lateinit var searchAdapter: SearchAdapter

    private var users = ArrayList<UserInfo>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onBackPressedListener = activity as OnBackPressedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement DrawerLocker")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init toolbar
        initToolbar()

        // Init repository
        appRepository = AppRepository(requireContext())

        // Init searchView
        initSearchView()

        // Search list
        initRecycler()
    }

    private fun initSearchView() {
        // open search view
        binding.search.onActionViewExpanded()

        // search view text change listener
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.search.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText.isNotEmpty()) { // Searching
                        val list = appRepository.findUsers(it)
                        if (list.isNotEmpty()) { // Find something
                            findSomething(list, newText)
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

    private fun nothingInput() {
        visibilityOnNothingInput()
        binding.errorText.text = getText(R.string.empty_recent_search)
        searchAdapter.setItem(ArrayList(), null)
    }

    private fun visibilityOnFindSomething() {
        binding.titleText.visibility = View.VISIBLE
        binding.descriptionText.visibility = View.VISIBLE
        binding.errorText.visibility = View.INVISIBLE
        binding.searchList.visibility = View.VISIBLE
    }

    private fun visibilityOnNothingFind() {
        binding.titleText.visibility = View.GONE
        binding.descriptionText.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.searchList.visibility = View.GONE
    }

    private fun visibilityOnNothingInput() {
        binding.titleText.visibility = View.GONE
        binding.descriptionText.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
    }

    private fun initToolbar() {
        // Set toolbar and menu
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)

        // Set action for menu icon
        binding.toolbar.setNavigationOnClickListener {
            hideKeyboard(requireContext(), it)
            onBackPressedListener.doBack()
        }
    }

    private fun initRecycler() {
        searchAdapter = SearchAdapter(users)
        binding.searchList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
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
}