package com.jakode.contacts.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakode.contacts.R
import com.jakode.contacts.databinding.FragmentSearchBinding
import com.jakode.contacts.utils.DrawerManager

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
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
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerManager.unlockDrawer()
    }
}