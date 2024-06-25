/**
 * Student ID: 2940038
 */

package com.example.diaryapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.diaryapp.databinding.Fragment1PickDateBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Fragment1PickDate : Fragment() {

    // uses view binding technique to bind the layout to the fragment and use the elements from the UI easily
    private var _binding: Fragment1PickDateBinding? = null
    private val binding get() = _binding!!

    // declare a variable to hold an instance of MyViewModel
    lateinit var viewModel: MyViewModel

    // initializes the databaseAdapter
    private lateinit var databaseAdapter: DatabaseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment1PickDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // creates an instance of the databaseAdapter
        databaseAdapter = DatabaseAdapter(requireContext())

        // initialize the viewModel
        viewModel = activity?.run {
            ViewModelProvider(this)[MyViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        // handles the next button
        binding.nextButton.setOnClickListener {
            val selectedDate = binding.editDateText.text.toString()

            // check if the entered date is in the correct format and is valid
            if (isValidDate(selectedDate)) {
                // check if a note with the selected date already exists
                if (databaseAdapter.doesNoteExist(selectedDate)) {
                    Toast.makeText(requireContext(), "Note for this date already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // set the selected date in view model to pass it to fragment2
                    viewModel.setSelectedDate(selectedDate)

                    // calls the moveToFragment2() method from MainActivity to transition to fragment3
                    // passes the selected date as the argument for the next fragment
                    (activity as? MainActivity)?.moveToFragment2()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a valid date in the format YYYY-MM-DD", Toast.LENGTH_SHORT).show()
            }
        }

        // handles the view all notes button
        binding.viewAllNotesButton1.setOnClickListener {
            // calls the moveToFragment3() method from MainActivity to transition to fragment3
            (activity as? MainActivity)?.moveToFragment3()
        }

        // handles the select date button
        binding.selectDateButton.setOnClickListener {
            // calls the date picker dialog method to let the user select a date
            displayDatePicker()
        }
    }

    /**
     * Displays the date picker dialog box
     */
    private fun displayDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // gets the date and month formatted to MM and DD properly
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)

                // gets the selected date
                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                binding.editDateText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    /**
     * Checks if the date is valid in the format YYYY-MM-DD
     * @param date, the date whose validity is to be checked
     * @return a boolean to show if the date is valid or not
     */
    private fun isValidDate(date: String): Boolean {
        // create a SimpleDateFormat object with the date format yyyy-mm-dd
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        dateFormat.isLenient = false

        return try {
            val parsedDate = dateFormat.parse(date)

            // checks if the parsed date is not null
            if (parsedDate != null) {
                // formats the date back to string
                val formattedDate = dateFormat.format(parsedDate)

                // compares the date with the formatted date to check if the format is right
                date == formattedDate
            } else {
                false
            }
        } catch (e: ParseException) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
