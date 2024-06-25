/**
 * Student ID: 2940038
 */

package com.example.diaryapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.diaryapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // uses view binding technique to bind the layout to the activity and use the elements from the UI easily
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // sets up the theme of the app based on user preference
        setAppTheme()

        if (savedInstanceState == null) {
            // start with fragment1
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Fragment1PickDate())
                .commit()
        }

        // set a listener for the switch
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch to Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                // saves the mode of the app
                saveAppState(true)
            } else {
                // Switch to Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                // saves the mode of the app
                saveAppState(false)
            }
            recreate()
        }
    }

    // Move to Fragment1PickDate and place it in the fragment container
    fun moveToFragment1() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Fragment1PickDate())
            .addToBackStack(null)
            .commit()
    }

    // Move to Fragment2AddNote and place it in the fragment container
    fun moveToFragment2() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Fragment2AddNote())
            .addToBackStack(null)
            .commit()
    }

    // Move to Fragment3ViewNote and place it in the fragment container
    fun moveToFragment3() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Fragment3ViewNote())
            .addToBackStack(null)
            .commit()
    }

    /**
     * Sets the theme of the app at the start to dark or light mode from the shared preference or previous visit state
     */
    private fun setAppTheme() {
        // checks the state of the app in the previous visit and sets the switch mode based on the result
        binding.themeSwitch.isChecked = isDarkModeOn()

        // sets up the theme of the app based on user preference
        if (isDarkModeOn()) {
            binding.themeSwitch.text = "Dark Mode"
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            binding.themeSwitch.text = "Light Mode"
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * Retrieves the state of the mode dark/light in the shared preferences
     */
    private fun isDarkModeOn(): Boolean {
        // retrieve the current theme mode from shared preferences
        val sharedPref = getPreferences(MODE_PRIVATE)
        return sharedPref.getBoolean("dark_mode", false)
    }

    /**
     * Saves the state of the app dark/light in the shared preferences
     */
    private fun saveAppState(isDarkModeEnabled: Boolean) {
        // saves the current theme mode to shared preferences
        val sharedPref = getPreferences(MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("dark_mode", isDarkModeEnabled)
            apply()
        }
    }
}
