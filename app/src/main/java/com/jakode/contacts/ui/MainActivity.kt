package com.jakode.contacts.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.jakode.contacts.R
import com.jakode.contacts.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.fragment_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val navController by lazy {
        findNavController(R.id.fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Drawer layout animation
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
                basic_layout.translationX = slideX
            }
        })

        // Management destination of fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addUserFragment -> {
//                    binding.appBarMain.appbar.setExpanded(false)
                }
            }
        }
    }
}