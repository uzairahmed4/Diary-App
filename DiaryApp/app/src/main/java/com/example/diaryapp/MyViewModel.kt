/**
 * Student ID: 2940038
 */

package com.example.diaryapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private val _selectedDate = MutableLiveData<String>()

    val selectedDate: MutableLiveData<String>
        get() = _selectedDate

    init {
        // sets the value of the selected date
        _selectedDate.value = ""
    }

    /**
     * Set the selected date for the diary entry
     * @param date The selected date for the entry in yyyy-mm-dd format
     */
    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }
}
