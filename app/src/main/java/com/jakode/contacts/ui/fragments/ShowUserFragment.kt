package com.jakode.contacts.ui.fragments

import android.content.Context
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jakode.contacts.R
import com.jakode.contacts.databinding.FragmentShowUserBinding
import com.jakode.contacts.utils.DrawerManager

class ShowUserFragment : Fragment() {
    private lateinit var binding: FragmentShowUserBinding
    private lateinit var drawerManager: DrawerManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerManager = activity as DrawerManager
        } catch (e: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement DrawerLocker")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementAnim()
    }

    private fun sharedElementAnim() {
        // Shared element anim
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.change_bounds)
        sharedElementReturnTransition = Fade()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        drawerManager.lockDrawer()
        binding = FragmentShowUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerManager.unlockDrawer()
    }
}