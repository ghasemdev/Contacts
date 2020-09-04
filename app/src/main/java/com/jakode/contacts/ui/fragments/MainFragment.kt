package com.jakode.contacts.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakode.contacts.R
import com.jakode.contacts.adapter.RecentAdapter
import com.jakode.contacts.data.model.Recent
import com.jakode.contacts.data.model.User
import com.jakode.contacts.databinding.FragmentMainBinding
import com.jakode.contacts.utils.DrawerManager
import com.jakode.contacts.utils.PopupMenu

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var drawerManager: DrawerManager
    private lateinit var viewAdapter: RecentAdapter

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

        // orientation and adapter
        val viewManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = RecentAdapter(users())

        // Recycler view set setting
        binding.toolbarHeader.recentList.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // Init clickable
        clickListener()
    }

    private fun clickListener() {
        // Cover click listener
        binding.toolbarHeader.myCover.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToShowUserFragment()
            val extras = FragmentNavigatorExtras( // Shared element animation
                binding.toolbarHeader.myCover to "imageView"
            )
            findNavController().navigate(action, extras)
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
                PopupMenu.show(anchor, 0, -125)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun users() = arrayListOf(
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        ),
        Recent(
            User(
                "https://avatars3.githubusercontent.com/u/65798992?s=400&u=bfa1e1b2c65f62934c84f0591e0bc3d8e677bb48&v=4",
                "Jack",
                "Blue"
            ), "1 hr ago"
        )
    )
}