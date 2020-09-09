package com.jakode.contacts.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakode.contacts.R
import com.jakode.contacts.adapter.EmailAdapter
import com.jakode.contacts.adapter.PhoneAdapter
import com.jakode.contacts.data.model.Name
import com.jakode.contacts.data.model.Profile
import com.jakode.contacts.data.model.User
import com.jakode.contacts.data.model.UserAndProfile
import com.jakode.contacts.data.repository.AppRepository
import com.jakode.contacts.databinding.FragmentAddUserBinding
import com.jakode.contacts.utils.DrawerManager
import com.jakode.contacts.utils.ImageUtil
import com.jakode.contacts.utils.PickerDate
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*
import kotlin.collections.ArrayList

class AddUserFragment : Fragment(), TextWatcher {
    private lateinit var binding: FragmentAddUserBinding
    private lateinit var appRepository: AppRepository
    private lateinit var drawerManager: DrawerManager

    private lateinit var phoneAdapter: PhoneAdapter
    private lateinit var emailAdapter: EmailAdapter

    private var phonesList = ArrayList<String>()
    private var emailsList = ArrayList<String>()

    private var imageUri: Uri? = null
    private lateinit var imageDefault: String

    companion object {
        const val REQ_CONTENT = 1
    }

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
        // init repository
        appRepository = AppRepository(requireContext())

        // Set default background cover
        imageDefault = ImageUtil.setDefaultImage(requireContext(), binding.cover)

        // RecyclerViews
        initRecycler()

        // OnClick phone and email field
        phoneOnClick()
        emailOnClick()

        // Text change listener for first name and last name
        binding.firstName.addTextChangedListener(this)
        binding.lastName.addTextChangedListener(this)

        binding.cover.setOnClickListener {
            // Open gallery
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                it.type = "image/*"
                startActivityForResult(it, REQ_CONTENT)
            }
        }

        binding.birthdayText.setOnClickListener {
            PickerDate.apply {
                textView = binding.birthday
                calendarType =
                    if (Locale.getDefault().language == "fa") DatePickerDialog.Type.JALALI
                    else DatePickerDialog.Type.GREGORIAN
                setFont(requireActivity(), R.font.iran_sans_mobile_fa_num)
                modeDarkDate = isDarkTheme(requireActivity())
                show(parentFragmentManager, "date picker")
            }
        }

        binding.cancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.save.setOnClickListener {
            if (isValid()) addUser()
        }
    }

    private fun addUser() {
        val firstName = binding.firstName.text.toString().trim()    // NotNull
        val lastName = binding.lastName.text.toString().trim()      // NotNull
        val birthday = binding.birthday.hint?.toString()?.trim()
        val address = with(binding.address.text.toString().trim()) {
            if (this.isNotEmpty()) this else null
        }
        val description = with(binding.description.text.toString().trim()) {
            if (this.isNotEmpty()) this else null
        }

        fun saveImage() = ImageUtil.saveFilePrivate(requireContext(), imageUri!!)
        fun phones() = phonesList.map { it.trim() }.filter { it.isNotEmpty() }.toList()
        fun emails() = if (emailsList.isNotEmpty()) emailsList.map { it.trim() }
            .filter { it.isNotEmpty() }.toList() else emailsList

        val isBlock = false
        val isTrash = false
        appRepository.insertUser(
            UserAndProfile(
                User(Name(firstName, lastName), isBlock, isTrash),
                Profile(
                    imageUri?.let { saveImage() } ?: imageDefault,
                    birthday,
                    address,
                    description
                )
            ), phones(), emails()
        )
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

    private fun isValid(): Boolean {
        return when {
            binding.firstName.text!!.isEmpty() -> { // When first name is empty
                binding.TILFirstName.error = resources.getString(R.string.first_name_error)
                false
            }
            binding.lastName.text!!.isEmpty() -> { // When last name is empty
                binding.TILLastName.error = resources.getString(R.string.last_name_error)
                false
            }
            !phonesListIsValid() -> false // When phones not valid
            !emailListIsValid() -> false // When emails not valid
            else -> true
        }
    }

    private fun phonesListIsValid(): Boolean {
        return when {
            // When phone list or phone is empty
            phonesList.isEmpty() || phonesList.map { it.trim() }.filter { it.isNotEmpty() }
                .count() == 0 -> {
                Toast.makeText(
                    requireContext(),
                    requireContext().getText(R.string.phone_empty_error),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> { // Then validation of phones
                for (index in phonesList.indices) {
                    val phone = phonesList[index]
                    if (phone.isNotEmpty()) {
                        if (!Patterns.PHONE.matcher(phone).matches()) {
                            phonesList[index] = "ERROR${phonesList[index]}"
                            phoneAdapter.notifyDataSetChanged()
                            return false
                        }
                    }
                }
                return true
            }
        }
    }

    private fun emailListIsValid(): Boolean {
        // Emails validation
        for (index in emailsList.indices) {
            val email = emailsList[index].trim()
            if (email.isNotEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailsList[index] = "ERROR${emailsList[index]}"
                    emailAdapter.notifyDataSetChanged()
                    return false
                }
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerManager.unlockDrawer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CONTENT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageUri = data.data!!
                    ImageUtil.setImage(imageUri!!, binding.cover)
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        binding.TILFirstName.isErrorEnabled = false
        binding.TILLastName.isErrorEnabled = false
    }

    override fun afterTextChanged(s: Editable?) {
    }
}