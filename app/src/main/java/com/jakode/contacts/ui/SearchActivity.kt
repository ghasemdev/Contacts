package com.jakode.contacts.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.jakode.contacts.R
import com.jakode.contacts.databinding.ActivitySearchBinding
import com.jakode.contacts.utils.manager.NavigateManager
import com.jakode.contacts.utils.manager.OnBackPressedListener

class SearchActivity : AppCompatActivity(), NavigateManager, OnBackPressedListener {
    private lateinit var binding: ActivitySearchBinding
    private val navController by lazy {
        findNavController(R.id.fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun doBack() {
        onBackPressed()
    }

    override fun navigateUp() {
        onBackPressed()
    }
}