package com.jakode.contacts.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakode.contacts.R
import com.jakode.contacts.adapter.EmailAdapter
import com.jakode.contacts.adapter.PhoneAdapter
import com.jakode.contacts.adapter.model.Item
import com.jakode.contacts.data.model.UserInfo
import com.jakode.contacts.databinding.FragmentShowUserBinding
import com.jakode.contacts.utils.DateConverter
import com.jakode.contacts.utils.ImageUtil
import com.jakode.contacts.utils.Intents
import com.jakode.contacts.utils.dialog.BottomSheet
import com.jakode.contacts.utils.dialog.PopupMenu
import java.util.*
import kotlin.collections.ArrayList

class ShowUserFragment : Fragment() {
    private lateinit var binding: FragmentShowUserBinding
    private lateinit var userInfo: UserInfo
    private val args: ShowUserFragmentArgs by navArgs()

    private lateinit var phoneAdapter: PhoneAdapter
    private lateinit var emailAdapter: EmailAdapter

    private var phonesList = ArrayList<Item>()
    private var emailsList = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // user received from add fragment
        userInfo = args.user

        // Init lists
        for (phone in userInfo.phones) phonesList.add(Item(1, phone))
        for (email in userInfo.emails) emailsList.add(Item(1, email))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowUserBinding.inflate(layoutInflater)

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
            findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerViews
        initRecycler()

        // Init field
        initialize()

        // OnClick
        onClick()
    }

    private fun onClick() {
        // Header icon
        headerOnClick()
        // Footer icon
        footerOnClick()
        // Body
        boxOnClick()
        // Navigation
        navOnClick()
    }

    private fun headerOnClick() {
        binding.callIcon.setOnClickListener {
            Intents.dialPhoneNumber(requireContext(), userInfo.phones[0])
        }
        binding.massageIcon.setOnClickListener {
            Intents.composeSmsMessage(requireContext(), userInfo.phones[0])
        }
        binding.duoIcon.setOnClickListener {
            Intents.dialGoogleDuo(requireContext(), userInfo.phones[0])
        }
        binding.emailIcon.setOnClickListener {
            Intents.composeEmail(requireContext(), arrayOf(userInfo.emails[0]), "", "")
        }
    }

    private fun footerOnClick() {
        binding.birthdayIcon.setOnClickListener {
            val (firstName, lastName) = userInfo.user.name.split(";;")
            val title = "${requireContext().getString(R.string.birthday)} " + "$firstName $lastName"
            val time = convertDate(userInfo.profile.birthday!!)

            Intents.addEvent(requireContext(), title, "", "", time, time)
        }
        binding.addressIcon.setOnClickListener {
            Intents.showMap(requireContext(), userInfo.profile.address!!)
        }
    }

    private fun boxOnClick() {
        binding.birthdayBox.setOnClickListener {
            Intents.showEvent(requireContext(), convertDate(userInfo.profile.birthday!!))
        }
        binding.addressBox.setOnClickListener {
            Intents.showMap(requireContext(), userInfo.profile.address!!)
        }
    }

    private fun navOnClick() {
        binding.share.setOnClickListener {
            BottomSheet(
                BottomSheet.Type.BOTTOM_SHARE,
                requireActivity(),
                R.style.BottomSheetDialogTheme,
                userInfo
            ).show()
        }

        binding.edit.setOnClickListener {
            val action =
                ShowUserFragmentDirections.actionShowUserFragmentToEditUserFragment(userInfo)
            findNavController().navigate(action)
        }
    }

    private fun initialize() {
        cover()
        name()
        phone()
        emailVisibility()
        birthday()
        address()
        description()
    }

    private fun cover() {
        val coverName = userInfo.profile.cover
        if (coverName.contains("cover")) {
            ImageUtil.setDefaultImage(requireContext(), binding.cover, coverName)
        } else {
            val uri = ImageUtil.loadFilePrivate(requireContext(), coverName).toUri()
            ImageUtil.setImage(uri, binding.cover)
        }
    }

    private fun name() {
        val (firstName, lastName) = userInfo.user.name.split(";;")
        binding.firstName.text = firstName
        binding.lastName.text = lastName
    }

    private fun phone() {
        binding.phone.text = userInfo.phones[0]
    }

    private fun emailVisibility() {
        if (userInfo.emails.isNotEmpty()) binding.emailIcon.visibility = View.VISIBLE
    }

    private fun birthday() {
        if (userInfo.profile.birthday == null) {
            binding.birthdayBox.visibility = View.GONE
        } else {
            binding.showBirthday.text = userInfo.profile.birthday
        }
    }

    private fun address() {
        if (userInfo.profile.address == null) {
            binding.addressBox.visibility = View.GONE
        } else {
            binding.showAddress.text = userInfo.profile.address
        }
    }

    private fun description() {
        if (userInfo.profile.description == null) {
            binding.descriptionBox.visibility = View.GONE
        } else {
            binding.showDescription.text = userInfo.profile.description
        }
    }

    private fun initRecycler() {
        // Phone list
        phoneAdapter = PhoneAdapter(phonesList)
        binding.phonesList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = phoneAdapter
        }

        // Email list
        emailAdapter = EmailAdapter(emailsList)
        binding.emailsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = emailAdapter
        }
    }

    private fun convertDate(data: String): Long {
        val date = data.split("/").map { it.toInt() }.toMutableList()
        if (Locale.getDefault().language == "fa") {
            DateConverter().apply {
                persianToGregorian(date[0], date[1], date[2])
                date[0] = year
                date[1] = month
                date[2] = day
            }
        }

        return Calendar.getInstance().run {
            set(get(Calendar.YEAR), date[1] - 1, date[2], 0, 0, 0)
            this.timeInMillis
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.show_user_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_user_more -> {
                // Open more options
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
                    PopupMenu.Type.SHOW_USER_POPUP,
                    userInfo,
                    anchor,
                    x, y,
                    selectionManager = null,
                    buttonBox = null
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}