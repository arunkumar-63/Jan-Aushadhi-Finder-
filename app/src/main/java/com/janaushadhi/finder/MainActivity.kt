package com.janaushadhi.finder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.janaushadhi.finder.databinding.ActivityMainBinding
import com.janaushadhi.finder.ui.home.HomeFragment
import com.janaushadhi.finder.ui.map.StoreMapFragment
import com.janaushadhi.finder.ui.reminder.ReminderFragment
import com.janaushadhi.finder.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        applyStoredLocale()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showFragment(HomeFragment())
                R.id.nav_search -> showFragment(SearchFragment())
                R.id.nav_map -> showFragment(StoreMapFragment())
                R.id.nav_reminder -> showFragment(ReminderFragment())
            }
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun applyStoredLocale() {
        val languageTag = getSharedPreferences("app_settings", MODE_PRIVATE)
            .getString("language_tag", "")
            .orEmpty()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}
