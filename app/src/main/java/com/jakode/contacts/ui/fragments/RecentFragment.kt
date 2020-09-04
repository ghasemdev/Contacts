package com.jakode.contacts.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakode.contacts.databinding.FragmentRecentBinding
import com.jakode.contacts.utils.DrawerManager

class RecentFragment : Fragment() {
    private lateinit var binding: FragmentRecentBinding
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
        drawerManager.lockDrawer()
        binding = FragmentRecentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerManager.unlockDrawer()
    }
}