package com.jakode.contacts.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakode.contacts.R
import com.jakode.contacts.adapter.EmailAdapter
import com.jakode.contacts.adapter.PhoneAdapter
import com.jakode.contacts.databinding.FragmentAddUserBinding
import com.jakode.contacts.utils.DrawerManager

class AddUserFragment : Fragment() {
    private lateinit var binding: FragmentAddUserBinding
    private lateinit var drawerManager: DrawerManager

    private lateinit var phoneAdapter: PhoneAdapter
    private lateinit var emailAdapter: EmailAdapter

    private var phonesList = ArrayList<String>()
    private var emailsList = ArrayList<String>()

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
        binding = FragmentAddUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerViews
        initRecycler()

        // OnClick phone and email field
        phoneOnClick()
        emailOnClick()

        binding.cancel.setOnClickListener {
            hideKeyboard(requireContext(), it)
            findNavController().navigateUp()
        }
    }

    private fun initRecycler() {
        // Phone list
        phoneAdapter =
            PhoneAdapter(phonesList, binding.phoneAddIcon, binding.phoneDivider)
        binding.phonesList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = phoneAdapter
        }

        // Email list
        emailAdapter = EmailAdapter(emailsList, binding.emailAddIcon, binding.emailDivider)
        binding.emailsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = emailAdapter
        }
    }

    private fun phoneOnClick() {
        binding.phoneText.setOnClickListener {
            phoneAdapter.iconDisplay()

            // First time add a item
            if (phonesList.size == 0) phoneAdapter.addItem()
        }

        binding.phoneAddIcon.setOnClickListener {
            // Max phone number is 10
            if (phonesList.size < 10) phoneAdapter.addItem() else Toast.makeText(
                requireContext(),
                requireContext().getText(R.string.ten_add_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun emailOnClick() {
        binding.emailText.setOnClickListener {
            emailAdapter.iconDisplay()

            // First time add a item
            if (emailsList.size == 0) emailAdapter.addItem()
        }

        binding.emailAddIcon.setOnClickListener {
            // Max email is 10
            if (emailsList.size < 10) emailAdapter.addItem() else Toast.makeText(
                requireContext(),
                requireContext().getText(R.string.ten_add_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerManager.unlockDrawer()
    }
}