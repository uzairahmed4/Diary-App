/**
 * Student ID: 2940038
 */

package com.example.diaryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.diaryapp.databinding.Fragment2AddNoteBinding

class Fragment2AddNote : Fragment() {

    // uses view binding technique to bind the layout to the fragment and use the elements from the UI easily
    private var _binding: Fragment2AddNoteBinding? = null
    private val binding get() = _binding!!

    // declare a variable to hold an instance of MyViewModel
    lateinit var viewModel: MyViewModel

    // initializes the databaseAdapter
    private lateinit var databaseAdapter: DatabaseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment2AddNoteBinding.inflate(inflater, container, false)
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

        // retrieves the selected date from viewModel
        val selectedDate = viewModel.selectedDate.value
        binding.selectedDateView.text = "$selectedDate"

        // handles the view all notes button
        binding.viewAllNotesButton2.setOnClickListener {
            // calls the moveToFragment3() method from MainActivity to transition to fragment3
            (activity as? MainActivity)?.moveToFragment3()
        }

        // handles the back button
        binding.backButton.setOnClickListener {
            // calls the moveToFragment1() method from MainActivity to transition to fragment1
            (activity as? MainActivity)?.moveToFragment1()
        }

        // handles the clear button
        binding.clearButton.setOnClickListener {
            binding.editNoteText.text.clear()
        }

        // handles the next button
        binding.nextButton2.setOnClickListener {
            val noteData = binding.editNoteText.text.toString().trim()

            // checks if the entered text is empty or not
            if (noteData.isNotEmpty()) {
                // adds the note in the database
                val noteId = databaseAdapter.addNote(selectedDate!!, noteData)

                if (noteId != -1L) {
                    Toast.makeText(requireContext(), "Note added successfully", Toast.LENGTH_SHORT).show()
                    (activity as? MainActivity)?.moveToFragment3()
                } else {
                    Toast.makeText(requireContext(), "Error adding note", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
