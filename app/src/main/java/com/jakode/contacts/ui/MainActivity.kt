package com.jakode.contacts.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jakode.contacts.R
import com.jakode.contacts.databinding.ActivityMainBinding
import com.jakode.contacts.utils.DrawerManager

class MainActivity : AppCompatActivity(), DrawerManager {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val navController by lazy {
        findNavController(R.id.fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Drawer layout animation
        animDrawer()

        // Init navigation view
        navViewSetup()

        // Management destination of fragment
        fragmentManager()
    }

    private fun navViewSetup() {
        // Use nav graph for translation
        appBarConfiguration = AppBarConfiguration(
            navController.graph, binding.drawerLayout
        )

        // Set nav graph for navigation view
        binding.navView.setupWithNavController(navController)
    }

    private fun fragmentManager() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment -> {
                    unlockDrawer()
                }
                R.id.addUserFragment -> {
                    lockDrawer()
                }
                R.id.adminFragment -> {
                    lockDrawer()
                }
                R.id.recentFragment -> {
                    lockDrawer()
                }
                R.id.searchFragment -> {
                    lockDrawer()
                }
                R.id.showUserFragment -> {
                    lockDrawer()
                }
                R.id.editUserFragment -> {
                    lockDrawer()
                }
            }
        }
    }

    // when click back close drawer
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    override fun lockDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun unlockDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun animDrawer() {
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
                binding.contentMain.content.translationX = slideX
            }
        })
    }
}