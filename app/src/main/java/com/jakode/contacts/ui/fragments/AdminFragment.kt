package com.jakode.contacts.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jakode.contacts.databinding.FragmentAdminBinding
import com.jakode.contacts.utils.ImageSetter

class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init app bar image
        ImageSetter.set(
            "https://i.redd.it/hfdbbih4nou41.jpg",
            binding.appBarImage
        )

        // Back button
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}