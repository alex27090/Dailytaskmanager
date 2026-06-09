package com.dailytask.manager.ui.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.dailytask.manager.R
import com.dailytask.manager.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.category_nav_host_fragment) as NavHostFragment
        navController = navHost.navController


        val appBarConfig = AppBarConfiguration(setOf(R.id.categoryListFragment))
        setupActionBarWithNavController(navController, appBarConfig)


        binding.bottomNav.selectedItemId = R.id.categoryListFragment
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    finish()
                    true
                }
                R.id.categoryListFragment -> {

                    navController.popBackStack(R.id.categoryListFragment, false)
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
